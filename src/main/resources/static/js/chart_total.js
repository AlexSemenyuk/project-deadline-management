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
    console.dir(projectChart);

    // const tmpp = projectChart.project.start;
    // console.log("tmpp" + tmpp);

    // Работа с графиком №1
    const data = {
        labels: ['Конструктор', 'Технолог', 'Контракти', 'Виробництво', 'Проект загалом'],
        datasets: [{
            label: 'Термін виконання етапів проекту',
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
                'rgba(153, 102, 255, 1)'
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
                        min: projectChart.project.start,
                        type: 'time',
                        time: {
                            unit: 'day'
                        }
                    },
                    y: {
                        beginAtZero: true
                    }
                },
                plugins: {
                    title: {
                        display: true,
                        text: 'Графік проекту',
                        font: {
                            size: 18, // Adjust the font size for the chart title
                        },
                    },
                    legend: {
                        display: true,
                        labels: {
                            font: {
                                size: 16, // Adjust the font size for the legend labels
                            },
                        },
                    },
                },
                layout: {
                    // padding: {
                    //     left: 20,
                    //     right: 20,
                    //     top: 20,
                    //     bottom: 20,
                    // },
                },
                responsive: true, // Allow the chart to be responsive
                maintainAspectRatio: false, // Override default aspect ratio behavior
                width: 800, // Set the desired width of the chart
                height: 300, // Set the desired height of the chart
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

document.addEventListener('DOMContentLoaded', function () {
    const chartDetails = document.getElementById('chartDetails');
    // const id = chartDetails.name;
    // console.log('click id = ' + id);

    // Add a click event listener to the summary element of the details
    chartDetails.querySelector('summary').addEventListener('click', function (ev) {
        // Toggle the open/closed state of the details section
        // chartDetails.open = !chartDetails.open;
        const id = chartDetails.getAttribute('name');
        console.log('click id = ', id);

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
});


// По событию click
// start.addEventListener('click', (e) => {
//     const id = e.target.name;
//     console.log('click id = ' + id);
//     const request = {
//         projectId: id
//     };
//     console.dir(request);
//
//     // Получили значения csrfToken
//     const csrfTokenInput = document.getElementById('csrfToken');
//     const csrfToken = csrfTokenInput.value;
//
//     // const url = "/api/v1/chart/" + id;
//
//     fetch(`/api/v1/chart/${id}`, {
//         method: 'POST',
//         headers: {
//             'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
//             'X-CSRF-TOKEN': csrfToken
//         },
//     })
//         .then(data => data.json())
//         .then(json => projectChartResponse(json));
//
// });








