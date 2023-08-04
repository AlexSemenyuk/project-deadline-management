// Получение данных для графика
const start = document.getElementById("start");

class ProjectChart {
    project
    design
    technology
    contracts
    production

    constructor(project, design, technology, contracts, production) {
        this.project = project;
        this.design = design;
        this.technology = technology;
        this.contracts = contracts;
        this.production = production;
    }

    get project() {
        return this.project;
    }

    set project(project) {
        this.project = project;
    }

    get design() {
        return this.design;
    }

    set design(design) {
        this.design = design;
    }

    get technology() {
        return this.technology;
    }

    set technology(technology) {
        this.technology = technology;
    }

    get contracts() {
        return this.contracts;
    }

    set contracts(contracts) {
        this.contracts = contracts;
    }

    get production() {
        return this.production;
    }

    set production(production) {
        this.production = production;
    }
}

function projectChartResponse(json) {
    console.log(json)
    // projectChart с данными в виде строки
    const project = json.project;
    const design = json.design;
    const technology = json.technology;
    const contracts = json.contracts;
    const production = json.production;
    const projectChart = new ProjectChart(project, design, technology, contracts, production);
    // projectChart с данными в виде Date
    // projectChart.project.start = new Date(project.start);
    // projectChart.project.deadline = new Date(project.deadline);
    // projectChart.design.start = new Date(design.start);
    // projectChart.design.deadline = new Date(design.deadline);
    // projectChart.technology.start = new Date(technology.start);
    // projectChart.technology.deadline = new Date(technology.deadline);
    // projectChart.contracts.start = new Date(contracts.start);
    // projectChart.contracts.deadline = new Date(contracts.deadline);
    // projectChart.production.start = new Date(production.start);
    // projectChart.production.deadline = new Date(production.deadline);
    console.dir(projectChart);

    const tmpp = projectChart.project.start;
    console.log("tmpp" + tmpp);

    // Работа с графиком №1
    const data = {
        labels: ['Design', 'Technology', 'Contracts', 'Production', 'Project in general'],
        datasets: [{
            label: 'Project deadline',
            data: [
                // ['2023-07-01', '2023-07-05'],
                [projectChart.design.start, projectChart.design.deadline],
                [projectChart.technology.start, projectChart.technology.deadline],
                [projectChart.contracts.start, projectChart.contracts.deadline],
                [projectChart.production.start, projectChart.production.deadline],
                [projectChart.project.start, projectChart.project.deadline]
            ],
            backgroundColor: [
                'rgba(75, 192, 192, 1)',
                'rgba(255, 206, 86, 1)',
                'rgb(81,169,21, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 26, 104, 1)',
                'rgba(153, 102, 255, 1)'
            ],
            borderColor: [
                'rgba(75, 192, 192, 1)',
                'rgba(255, 206, 86, 1)',
                'rgb(81,169,21, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 26, 104, 1)',
                'rgba(153, 102, 255, 1)',
                'rgba(255, 159, 64, 1)',
                'rgba(0, 0, 0, 1)'
            ],
            barPercentage: 0.8
            // borderWidth: 1
        }]
    };

    // config
        const config = {
            type: 'bar',
            data,
            options: {
                indexAxis: 'y',
                scales: {
                    x: {
                        min: '2023-07-01',
                        type: 'time',
                        time: {
                            unit: 'day'
                        }
                    },
                    y: {
                        beginAtZero: true
                    }
                }
            }
        };

    // render init block
        const myChart = new Chart(
            document.getElementById('myChart'),
            config
        );

    // Instantly assign Chart.js version
        const chartVersion = document.getElementById('chartVersion');
        chartVersion.innerText = Chart.version;

}

// По событию click
start.addEventListener('click', (e) => {
    const id = e.target.name;
    console.log('click id = ' + id);
    const request = {
        projectId: id
    };
    console.dir(request);

    // Получили значения csrfToken
    const csrfTokenInput = document.getElementById('csrfToken');
    const csrfToken = csrfTokenInput.value;

    // const url = "/api/v1/chart/" + id;

    fetch(`/api/v1/chart/${id}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
            'X-CSRF-TOKEN': csrfToken
        },
    })
        .then(data => data.json())
        .then(json => projectChartResponse(json));




});








