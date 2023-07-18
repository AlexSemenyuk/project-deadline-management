const ctx1 = document.getElementById('totalPlan').getContext('2d');
// ctx.canvas.parentNode.style.height = '600px';
// ctx.canvas.parentNode.style.width = '600px';

let labels = ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'];
let operationTimes = [12, 19, 3, 5, 2, 3];
let operationTimes1 = [18, 2, 10, 15, 2, 3];
new Chart(ctx1, {
    type: 'bar',
    data: {
        labels: labels,         // Значения по Х
        datasets: [{
            label: 'Project deadlines',         //  Подпись графика
            data: operationTimes,               //  Значения по Y
            backgroundColor: ['Red'],
            borderWidth: 1                      //  Толщина линии
        },
            {
                label: 'Project deadlines',         //  Подпись графика
                data: operationTimes1,         //  Значения по Y
                backgroundColor: ['Purple'],
                borderWidth: 1                      //  Толщина линии
            }]
    },
    options: {
        indexAxis: 'y',
        scales: {
            x: {
                stacked: true
            },
            y: {
                ticks: {
                    crossAlign: 'far',
                },
                stacked: true
            }
        }
    }
});

// Chart.defaults.backgroundColor = '#9BD0F5';      // Указан в функции
Chart.defaults.borderColor = '#36A2EB';             //
Chart.defaults.color = '#000';

const date = new Date();
const month = date.getMonth() + 1;
let day = month.getDay();
console.log(month);
console.log(day);

// --------------------------------------
var canvas = document.getElementById("totalPlan2");
var ctx = canvas.getContext("2d");

var circle = function (x, y, radius, z) {
    ctx.beginPath ();
    ctx.arc(x, y, radius, 0, Math.PI*2, false);
    if (z) {ctx.stroke();}
    else {ctx.fill();}
};

ctx.lineWidth = 2;
ctx.strokeStyle = "Black";


ctx.fillStyle = "Green";
circle (100, 100, 30, false);

ctx.fillStyle = "Orange";
circle (200, 100, 20, true);

ctx.fillStyle = "Blue";
circle (300, 100, 10, false);

















//
//
// var canvas = document.getElementById("totalPlan2");
// var ctx2 = canvas.getContext("2d");
//
// var circle = function (x, y, radius, z) {
//     ctx2.beginPath ();
//     ctx2.arc(x, y, radius, 0, Math.PI*2, false);
//     if (z) {ctx2.stroke();}
//     else {ctx2.fill();}
// };
//
// ctx2.lineWidth = 4;
// ctx2.strokeStyle = "Black";
//
// var drawSnowMan = function (x, y){
//     //Рисуем голову
//     circle (x, y, 20, true);
//     //Рисуем туловище
//     circle (x, y+20+30, 30, true);
//     //Рисуем глаза
//     ctx2.fillStyle = "Black";
//     circle (x-6, y-5, 4, false);
//     circle (x+6, y-5, 4, false);
//     //Рисуем рот
//     ctx2.fillStyle = "Yellow";
//     circle (x, y+5, 4, false);
//     //Рисуем пуговицы
//     ctx2.fillStyle = "Black";
//     circle (x, y+20+30-16, 4, false);
//     circle (x, y+20+30, 4, false);
//     circle (x, y+20+30+16, 4, false);
// };
// drawSnowMan(50,50);
// drawSnowMan(200,150);
// drawSnowMan(200,400);
//
//

