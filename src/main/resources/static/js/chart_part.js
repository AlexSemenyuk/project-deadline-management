class ProjectChart {
    projectNumber;
    partNumber;
    partName;
    termNumberList;
    termHoursList;

    constructor(projectNumber, partNumber, partName, termNumberList, termHoursList) {
        this.projectNumber = projectNumber;
        this.partNumber = partNumber;
        this.partName = partName;
        this.termNumberList = termNumberList;
        this.termHoursList = termHoursList;

    }

    get projectNumber() {
        return this.projectNumber;
    }

    set projectNumber(projectNumber) {
        this.projectNumber = projectNumber;
    }

    get partNumber() {
        return this.partNumber;
    }

    set partNumber(partNumber) {
        this.partNumber = partNumber;
    }

    get partName() {
        return this.partName;
    }

    set partName(partName) {
        this.partName = partName;
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

function projectChartResponse(json) {
    console.log(json)
    // projectChart с данными в виде строки
    const projectNumber = json.projectNumber;
    const partNumber = json.partNumber;
    const partName = json.partName;
    const termNumberList = json.termNumberList;
    console.dir(termNumberList);
    const termHoursList = json.termHoursList;
    console.dir(termHoursList);
    const projectChart = new ProjectChart(projectNumber, partNumber, partName, termNumberList, termHoursList);
    console.dir(projectChart);

    const resultArray = [];

// Проходим по всем ключам объекта
//
    const termHours = [];

    termHoursList.forEach(item => {
        const start = item.start;
        const deadline = item.deadline;
        termHours.push([start, deadline]);
    });

    const maxHeight = termHoursList.length * 200;
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
                    text: 'Графік технічних операцій щодо деталі',
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
            height: 500, // Set the desired height of the chart
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

        fetch(`/api/v1/chart_part/${id}`, {
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