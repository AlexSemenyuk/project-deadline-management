class ProjectAssemblyChart {
    projectNumber;
    assemblyNumber;
    assemblyName;
    termNumberList;
    termHoursList;

    constructor(projectNumber, assemblyNumber, assemblyName, termNumberList, termHoursList) {
        this.projectNumber = projectNumber;
        this.assemblyNumber = assemblyNumber;
        this.assemblyName = assemblyName;
        this.termNumberList = termNumberList;
        this.termHoursList = termHoursList;

    }

    get projectNumber() {
        return this.projectNumber;
    }

    set projectNumber(projectNumber) {
        this.projectNumber = projectNumber;
    }

    get assemblyNumber() {
        return this.assemblyNumber;
    }

    set assemblyNumber(assemblyNumber) {
        this.assemblyNumber = assemblyNumber;
    }

    get assemblyName() {
        return this.assemblyName;
    }

    set assemblyName(assemblyName) {
        this.assemblyName = assemblyName;
    }

    get termNumberList() {
        return this.termNumberList;
    }

    set termNumberList(termNumberList) {
        this.termNumberList = termNumberList;
    }

    get termHoursList() {
        return this.termHoursList;
    }

    set termHoursList(termHoursList) {
        this.termHoursList = termHoursList;
    }
}

function projectChartAssemblyResponse(json) {
    console.log(json)
    // projectChart с данными в виде строки
    const projectNumber = json.projectNumber;
    const assemblyNumber = json.assemblyNumber;
    const assemblyName = json.assemblyName;
    const termNumberList = json.termNumberList;
    console.dir(termNumberList);
    const termHoursList = json.termHoursList;
    console.dir(termHoursList);
    const projectAssemblyChart = new ProjectAssemblyChart(projectNumber, assemblyNumber, assemblyName, termNumberList, termHoursList);
    console.dir(projectAssemblyChart);

    const resultArray = [];

// Проходим по всем ключам объекта
    const termHours = [];

    termHoursList.forEach(item => {
        const start = item.start;
        const deadline = item.deadline;
        termHours.push([start, deadline]);
    });

    const maxHeight = termHoursList.length * 150;
    console.log(maxHeight);

    // Работа с графиком №1
    const data = {
        labels: termNumberList,
        datasets: [{
            label: 'Номер операції - час виконання операції (годин)',
            data: termHours,
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
                    min: 0,
                },
                y: {
                    beginAtZero: true
                }
            },
            plugins: {
                title: {
                    display: true,
                    text: 'Графік технічних операцій щодо вузла',
                    font: {
                        size: 16, // Adjust the font size for the chart title
                    },
                    options: {
                        scales: 'y',
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
            // projectId: id
            projectAndPartId: id
        };
        console.dir(request);

        // Получили значения csrfToken
        const csrfTokenInput = document.getElementById('csrfToken');
        const csrfToken = csrfTokenInput.value;

        // const url = "/api/v1/chart/" + id;

        fetch(`/api/v1/chart_assembly/${id}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
                'X-CSRF-TOKEN': csrfToken
            },
        })
            .then(data => data.json())
            .then(json => projectChartAssemblyResponse(json));
    });
});