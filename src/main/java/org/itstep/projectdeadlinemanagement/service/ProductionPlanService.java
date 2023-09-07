package org.itstep.projectdeadlinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.model.*;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductionPlanService {

    private final ProductionPlanRepository productionPlanRepository;
    private final EquipmentRepository equipmentRepository;
    private final ProjectRepository projectRepository;

    public void formProductionPlans(List<Task> tasks) {

        List<Equipment> equipmentList = equipmentRepository.findAll();
        List<ProductionPlan> productionPlans = productionPlanRepository.findAll();
        List<Project> projects = projectRepository.findAll();
        Project currentProject = null;

        // формирование ProductionPlans
        if (!equipmentList.isEmpty()) {
            for (Equipment equipment : equipmentList) {
                // Номер по списку ProductionPlans для данного оборудования
                int count = 0;

                // - Получение номера с последнего экземпляра существующего списка ProductionPlans для данного оборудования
                if (!productionPlans.isEmpty()) {
                    for (ProductionPlan productionPlan : productionPlans) {
                        if (Objects.equals(productionPlan.getEquipment().getNumber(), equipment.getNumber())) {
                            count++;
                        }
                    }
                }

                if (!tasks.isEmpty()) {
                    Integer projectNumber = tasks.get(0).getProjectNumber();
                    if (!projects.isEmpty()) {
                        for (Project project : projects) {
                            if (Objects.equals(project.getNumber(), projectNumber)) {
                                currentProject = project;
                                break;
                            }
                        }
                    }
                    for (Task task : tasks) {
                        if (Objects.equals(equipment.getNumber(), task.getEquipment().getNumber())) {
                            ProductionPlan productionPlan = new ProductionPlan(count + 1);
                            productionPlan.setTask(task);
                            productionPlan.setEquipment(equipment);
                            productionPlan.setCurrentStart(task.getStartProduction());
                            productionPlanRepository.save(productionPlan);
                            count++;
                        }
                    }
                }
            }
        }


        List<ProductionPlan> currentProductionPlans = productionPlanRepository.findAll();
        if (!currentProductionPlans.isEmpty()) {
            currentProductionPlans = currentProductionPlans.stream()
                    .sorted(Comparator.comparing(ProductionPlan::getNumber))
                    .collect(Collectors.toList());
        }

        formCurrentStartForPart(equipmentList, currentProductionPlans, currentProject);
        formCurrentStartForAssembly(equipmentList, currentProductionPlans, currentProject);
    }

    private void formCurrentStartForPart(List<Equipment> equipmentList, List<ProductionPlan> productionPlans, Project currentProject) {

        if (!equipmentList.isEmpty()) {
            for (Equipment equipment : equipmentList) {
                List<ProductionPlan> partProductionPlans = new ArrayList<>();
                List<ProductionPlan> assemblyProductionPlans = new ArrayList<>();

                // Виробництво - for Part
                if (equipment.getEquipmentType().getId() == 1) {
                    for (ProductionPlan productionPlan : productionPlans) {
                        if (Objects.equals(productionPlan.getEquipment().getNumber(), equipment.getNumber())) {
                            if (productionPlan.getTask().getTaskType().getId() == 1) {
                                partProductionPlans.add(productionPlan);
                            }
                        }
                    }
                    getPartQueue(partProductionPlans);
                }

            }
        }
    }

    private void formCurrentStartForAssembly(List<Equipment> equipmentList, List<ProductionPlan> productionPlans, Project currentProject) {

        // Определение окончания componentContracts
        LocalDateTime contractTMP = currentProject.getStart();
        for (Contract contract : currentProject.getContracts()) {
            if (contract.getContractType().getId() == 2 && contract.getDeadline().isAfter(contractTMP)) {
                contractTMP = contract.getDeadline();
            }
        }
//        System.out.println("contractTMP = " + contractTMP);

        // Определение окончания currentStart from partProductionPlans
        LocalDateTime deadlinePartProductionPlanTMP = currentProject.getStart();
        List<ProductionPlan> partProductionPlansFromCurrentProject = new ArrayList<>();

        for (ProductionPlan productionPlan : productionPlans) {
            if (Objects.equals(productionPlan.getTask().getProjectNumber(), currentProject.getNumber()) &&
                    productionPlan.getTask().getTaskType().getId() == 1) {
                partProductionPlansFromCurrentProject.add(productionPlan);
            }
        }

        for (ProductionPlan productionPlan : partProductionPlansFromCurrentProject) {
            LocalDateTime currentStartPartProductionPlan = TimeService.localDateTimeAddHours(productionPlan.getCurrentStart(),
                    productionPlan.getTask().getOperationTime());
            if (Objects.equals(productionPlan.getTask().getProjectNumber(), currentProject.getNumber()) &&
                    currentStartPartProductionPlan.isAfter(deadlinePartProductionPlanTMP)) {
                deadlinePartProductionPlanTMP = currentStartPartProductionPlan;
            }
        }
        deadlinePartProductionPlanTMP = TimeService.localDateTimeAddDays(deadlinePartProductionPlanTMP, 1);
//        System.out.println("deadlinePartProductionPlanTMP = " + deadlinePartProductionPlanTMP);

        // Определение max componentContracts и currentStart from partProductionPlans
        LocalDateTime assemblyProductionStart = null;
        if (contractTMP.isAfter(deadlinePartProductionPlanTMP)) {
            assemblyProductionStart = contractTMP;
        } else {
            assemblyProductionStart = deadlinePartProductionPlanTMP;
        }
//        System.out.println("assemblyProductionStart1 = " + assemblyProductionStart);
        assemblyProductionStart = TimeService.excludeWeekend(assemblyProductionStart.plusDays(1));
//        System.out.println("assemblyProductionStart2 = " +assemblyProductionStart);

        if (!equipmentList.isEmpty()) {
            for (Equipment equipment : equipmentList) {
                List<ProductionPlan> assemblyProductionPlans = new ArrayList<>();

                // Складання вузлів - for Assembly
                if (equipment.getEquipmentType().getId() == 2) {
                    for (ProductionPlan productionPlan : productionPlans) {
                        if (Objects.equals(productionPlan.getEquipment().getNumber(), equipment.getNumber())) {
                            if (productionPlan.getTask().getTaskType().getId() == 2) {
                                assemblyProductionPlans.add(productionPlan);
                            }
                        }
                    }
                }

                getAssemblyQueue(assemblyProductionPlans, assemblyProductionStart);
            }
        }
    }

    private void getPartQueue(List<ProductionPlan> productionPlans) {
        // currentDeadlineTmp - Дата + время, описывающее окончание предыдущего по списку ProductionPlans для данного оборудования
        // (CurrentStart с учетом OperationTime от предыдущего ProductionPlan с состоянием не "New")
        LocalDateTime[] currentDeadlineTmp = new LocalDateTime[1];
        currentDeadlineTmp[0] = null;

        // currentDeadlineFromPreviousTermNumberTmp является CurrentStart для текущего с учетом termNumber
        LocalDateTime[] currentDeadlineFromPreviousTermNumberTmp = new LocalDateTime[1];
        currentDeadlineFromPreviousTermNumberTmp[0] = null;


        for (ProductionPlan productionPlan : productionPlans) {
            if (!productionPlan.getTask().getTaskCondition().getName().equals("New")) {
                // Определение start для текущего (окончание предыдущего ProductionPlan)
                currentDeadlineTmp[0] = TimeService.localDateTimeAddHours(productionPlan.getCurrentStart(),
                        productionPlan.getTask().getOperationTime());
            } else {                                                // Работаем только с состоянием "New"
                // Проверка currentDeadline с предыдущего по TermNumber значения (partNumber = const, lotNumber=const)
                if (currentDeadlineTmp[0] != null) {                // Есть очередь (есть предыдущий) - в конец
                    // Коррекция по termNumber
                    if (productionPlan.getTask().getTermNumber() > 1) {
                        currentDeadlineFromPreviousTermNumberTmp[0] = getCurrentDeadlineFromPreviousTermNumber(productionPlan);
                        if (currentDeadlineFromPreviousTermNumberTmp[0].isAfter(currentDeadlineTmp[0])) {
                            currentDeadlineTmp[0] = currentDeadlineFromPreviousTermNumberTmp[0];
                        }
                    }
                    // Коррекция по StartProduction, в зависимости от design, technology and contract
                    if (productionPlan.getTask().getStartProduction().isAfter(currentDeadlineTmp[0])) {
                        currentDeadlineTmp[0] = productionPlan.getTask().getStartProduction();
                    }
                } else {                                    // Если нет очереди - дата task
                    currentDeadlineTmp[0] = productionPlan.getTask().getStartProduction();
                    currentDeadlineTmp[0] = TimeService.excludeWeekend(currentDeadlineTmp[0]);
                }
                productionPlan.setCurrentStart(currentDeadlineTmp[0]);
                productionPlanRepository.save(productionPlan);
                currentDeadlineTmp[0] = TimeService.localDateTimeAddHours(productionPlan.getCurrentStart(),
                        productionPlan.getTask().getOperationTime());
            }
        }
    }

    private void getAssemblyQueue(List<ProductionPlan> productionPlans, LocalDateTime assemblyProductionStart) {

        // currentDeadlineTmp - Дата + время, описывающее окончание предыдущего по списку ProductionPlans для данного оборудования
        // (CurrentStart с учетом OperationTime от предыдущего ProductionPlan с состоянием не "New")
        LocalDateTime[] currentDeadlineTmp = new LocalDateTime[1];
        currentDeadlineTmp[0] = null;

        for (ProductionPlan productionPlan : productionPlans) {
            if (!productionPlan.getTask().getTaskCondition().getName().equals("New")) {
                // Определение start для текущего (окончание предыдущего ProductionPlan)
                currentDeadlineTmp[0] = TimeService.localDateTimeAddHours(productionPlan.getCurrentStart(),
                        productionPlan.getTask().getOperationTime());
            } else {                                                // Работаем только с состоянием "New"
                if (currentDeadlineTmp[0] == null) {                // Если нет очереди - дата task
                    currentDeadlineTmp[0] = assemblyProductionStart;
                    currentDeadlineTmp[0] = TimeService.excludeWeekend(currentDeadlineTmp[0]);
                } else {
                    // Коррекция по assemblyProductionStart
                    if (assemblyProductionStart.isAfter(currentDeadlineTmp[0]) ) {
                        currentDeadlineTmp[0] = assemblyProductionStart;
                    }
                    // Проверка currentDeadline с предыдущего по TermNumber значения (partNumber = const, lotNumber=const)
                    if (productionPlan.getTask().getStartProduction().isAfter(currentDeadlineTmp[0]) ) {
                        currentDeadlineTmp[0] = productionPlan.getTask().getStartProduction();
                    }
                }
//                System.out.println("currentDeadlineTmp[0] = " + currentDeadlineTmp[0]);
                productionPlan.setCurrentStart(currentDeadlineTmp[0]);
                productionPlanRepository.save(productionPlan);
                currentDeadlineTmp[0] = TimeService.localDateTimeAddHours(productionPlan.getCurrentStart(),
                        productionPlan.getTask().getOperationTime());
            }
        }
    }

    // Сохранение цепочки termNumber - Получение currentDeadline с предыдущего по TermNumber значения (partNumber = const, lotNumber=const)
    private LocalDateTime getCurrentDeadlineFromPreviousTermNumber(ProductionPlan productionPlan) {
        LocalDateTime[] previousCurrentDeadline = new LocalDateTime[1];
        previousCurrentDeadline[0] = null;
        List<ProductionPlan> productionPlanList = productionPlanRepository.findAll();
        productionPlanList.forEach(plan -> {
            int termNumberCurrent = productionPlan.getTask().getTermNumber();
            int termNumberPrevious = plan.getTask().getTermNumber();
            if (Objects.equals(plan.getTask().getPartOrAssemblyNumber(), productionPlan.getTask().getPartOrAssemblyNumber()) &&
                    Objects.equals(plan.getTask().getLotNumber(), productionPlan.getTask().getLotNumber()) &&
                    termNumberCurrent == termNumberPrevious + 1) {
                previousCurrentDeadline[0] = TimeService
                        .localDateTimeAddHours(plan.getCurrentStart(), plan.getTask().getOperationTime());
//                        startAddOperationTime(plan);
            }
        });
        return previousCurrentDeadline[0];
    }


    public List<ProductionPlan> formPlansOfCurrentYear(LocalDate date) {
        int year = date.getYear();


        List<ProductionPlan> plansOfCurrentYear = new CopyOnWriteArrayList<>();
//        List<ProductionPlan> productionPlanList = productionPlanRepository.findByCurrentStartYear(year);

        List<ProductionPlan> productionPlanList = productionPlanRepository.findAll();
        productionPlanList.forEach(plan -> {
            if (plan.getCurrentStart().getYear() == year
//                    && plan.getCurrentStart().getMonthValue() == month
            ) {
                plansOfCurrentYear.add(plan);
            }
        });

        return plansOfCurrentYear;
    }


    public List<ProductionPlan> formPlansOfCurrentMonth(List<ProductionPlan> productionPlansPerCurrentYear, LocalDate date) {
        int month = date.getMonthValue();

        List<ProductionPlan> plansOfCurrentMonth = new CopyOnWriteArrayList<>();
        if (!productionPlansPerCurrentYear.isEmpty()) {
            productionPlansPerCurrentYear.forEach(plan -> {
                if (plan.getCurrentStart().getMonthValue() == month) {
                    plansOfCurrentMonth.add(plan);
                }
            });
        }
        return plansOfCurrentMonth;
    }

    public List<ProductionPlan> formPlansOfCurrentDate(List<ProductionPlan> productionPlansPerCurrentMonth, int dayNumber, int equipmentId) {
        List<ProductionPlan> plansOfCurrentDay = new CopyOnWriteArrayList<>();
        if (!productionPlansPerCurrentMonth.isEmpty()) {
            productionPlansPerCurrentMonth.forEach(plan -> {
                if (plan.getCurrentStart().getDayOfMonth() == dayNumber &&
                        plan.getEquipment().getId() == equipmentId) {
                    plansOfCurrentDay.add(plan);
                }
            });
        }
        return plansOfCurrentDay;
    }

    public void deleteProductionPlansForProject(Project project) {
        List<ProductionPlan> productionPlans = productionPlanRepository.findAll();
        for (ProductionPlan productionPlan: productionPlans){
            if (Objects.equals(productionPlan.getTask().getProjectNumber(), project.getNumber())){
                productionPlanRepository.delete(productionPlan);
            }
        }
    }
}






