package org.itstep.projectdeadlinemanagement.service;

import lombok.RequiredArgsConstructor;
import org.itstep.projectdeadlinemanagement.command.ChartDaysCommand;
import org.itstep.projectdeadlinemanagement.command.ChartMonthCommand;
import org.itstep.projectdeadlinemanagement.model.Equipment;
import org.itstep.projectdeadlinemanagement.model.ProductionPlan;
import org.itstep.projectdeadlinemanagement.model.Task;
import org.itstep.projectdeadlinemanagement.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public void formProductionPlans(List<Task> tasks) {

        List<Equipment> equipmentList = equipmentRepository.findAll();
        List<ProductionPlan> productionPlans = productionPlanRepository.findAll();

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
                    for (Task task : tasks) {
                        if (Objects.equals(equipment.getNumber(), task.getEquipment().getNumber())) {
                            ProductionPlan productionPlan = new ProductionPlan(count + 1);
                            productionPlan.setTask(task);
                            productionPlan.setEquipment(equipment);
//                            Optional<Equipment> optionalEquipment = equipmentRepository.findById(productionPlan.getTask().getEquipment().getId());
//                            optionalEquipment.ifPresent(productionPlan::setEquipment);
                            productionPlan.setCurrentStart(task.getStartProduction());
                            productionPlanRepository.save(productionPlan);
                            count++;
                        }
                    }
                }
            }
        }

        formCurrentStart();
    }

    private void formCurrentStart() {
        // Дата + время, описывающее окончание предыдущего по списку ProductionPlans для данного оборудования
        LocalDateTime[] currentDeadlineTmp = new LocalDateTime[2];
        List<Equipment> equipmentList = equipmentRepository.findAll();

        List<ProductionPlan> productionPlans = productionPlanRepository.findAll();
        if (!productionPlans.isEmpty()) {
            productionPlans = productionPlans.stream()
                    .sorted(Comparator.comparing(ProductionPlan::getNumber))
                    .collect(Collectors.toList());
        }

        if (!equipmentList.isEmpty()) {
            for (Equipment equipment : equipmentList) {
                // DateTime является CurrentStart для текущего (является Deadline, с учетом OperationTime от предыдущего ProductionPlan с состоянием не "New")
                currentDeadlineTmp[0] = null;
                // DateTime является CurrentStart для текущего с учетом termNumber
                currentDeadlineTmp[1] = null;

                productionPlans.forEach(plan -> {
                    if (Objects.equals(plan.getEquipment().getNumber(), equipment.getNumber())) {

                        if (!plan.getTask().getTaskCondition().getName().equals("New")) {
                            // Определение start для текущего (окончание предыдущего ProductionPlan)
                            currentDeadlineTmp[0] = TimeService
                                    .localDateTimeAddHours(plan.getCurrentStart(),
                                            plan.getTask().getOperationTime()
                                    );
                        } else {                                                // Работаем только с состоянием "New"
                            // Проверка currentDeadline с предыдущего по TermNumber значения (partNumber = const, lotNumber=const)
                            if (currentDeadlineTmp[0] != null) {                // Есть очередь (есть предыдущий) - в конец
                                // Коррекция по termNumber
                                if (plan.getTask().getTermNumber() > 1) {
                                    currentDeadlineTmp[1] = getCurrentDeadlineFromPreviousTermNumber(plan);
                                    if (currentDeadlineTmp[1].isAfter(currentDeadlineTmp[0])) {
                                        currentDeadlineTmp[0] = currentDeadlineTmp[1];
                                    }
                                }
                            } else {                                    // Если нет очереди - дата task
                                currentDeadlineTmp[0] = plan.getTask().getStartProduction();
                                currentDeadlineTmp[0] = TimeService.excludeWeekend(currentDeadlineTmp[0]);
                            }
                            plan.setCurrentStart(currentDeadlineTmp[0]);
                            productionPlanRepository.save(plan);
                            currentDeadlineTmp[0] = TimeService
                                    .localDateTimeAddHours(plan.getCurrentStart(),
                                            plan.getTask().getOperationTime()
                                    );
                        }
                    }
                });
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
       if (!productionPlansPerCurrentYear.isEmpty()){
           productionPlansPerCurrentYear.forEach(plan -> {
               if (plan.getCurrentStart().getMonthValue() == month ) {
                   plansOfCurrentMonth.add(plan);
               }
           });
       }
        return plansOfCurrentMonth;
    }

    public List<ProductionPlan> formPlansOfCurrentDate(List<ProductionPlan> productionPlansPerCurrentMonth, int dayNumber, int equipmentId) {
        List<ProductionPlan> plansOfCurrentDay = new CopyOnWriteArrayList<>();
        if (!productionPlansPerCurrentMonth.isEmpty()){
            productionPlansPerCurrentMonth.forEach(plan -> {
                if (plan.getCurrentStart().getDayOfMonth() == dayNumber &&
                plan.getEquipment().getId() == equipmentId) {
                    plansOfCurrentDay.add(plan);
                }
            });
        }
        return plansOfCurrentDay;
    }
}






