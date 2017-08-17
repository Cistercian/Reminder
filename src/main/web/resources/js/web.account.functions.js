/**
 * Created by Olaf on 30.04.2017.
 */
/**
 * функция очистки модального окна bootstrap
 * @constructor
 */
function ClearModal() {
    //удаляем прежние amount
    $('[id^="modalBody"]').each(function () {
        $(this).empty();
    });
    $('#modalHeader').empty();
    $('#modalHeader').append(
        "<div class='login-panel panel panel-default wam-not-padding '>" +
        "<div id='header' class='panel-heading'>" +
        "</div>" +
        "</div>");

    //рисуем структуру вывода данных
    $('#modalBody').append(
        "<div class='login-panel panel panel-default wam-not-padding '>" +
        "<div class='panel-body wam-not-padding'>" +
        "<div id='parentBars'" +
        "</div>" +
        "</div>" +
        "</div>");
}
/**
 * Функция вызова waitingDialog.js при выполнении ajax запросов
 */
function displayLoader() {
    waitingDialog.show('Загрузка...', {dialogSize: 'sm', progressType: 'warning'});
}
function hideLoader() {
    waitingDialog.hide();
}
/**
 * Функция очистки модального окна bootstrap
 *
 */
function ClearModalPanel() {
    //$('#modal').modal('hide');

    $('#modalTitle').text("");
    $('[id^="modalBody"]').each(function () {
        $(this).empty();
    });
    $('[id^="modalFooter"]').each(function () {
        $(this).empty();
    });
    //гарантированно чистим остатки всплывающего окна
    $('.modal-backdrop').each(function () {
        $(this).remove();
    });
    //гарантированно-гарантированно чистим остатки всплывающего окна
    $('body').removeClass('modal-open');

}
function setModalSize(type){
    if (type.indexOf("auto") !== -1) {
        //форматируем модальное окно под авторазмер
        $('.modal-lg').addClass('wam-modal-dialog');
        $('.modal-lg').removeClass('modal-dialog');
    } else {
        $('.modal-lg').addClass('modal-dialog');
        $('.modal-lg').removeClass('wam-modal-dialog');
    }
}
function displayMessage(type, message) {
    ClearModalPanel();

    var bodyContent, footerContent;
    if (type == 'sendEmail') {
        bodyContent =
            "<div class='col-xs-12'>" +
            "<strong>Укажите почтовый адрес получателя письма</strong></h4>" +
            "</div>" +
            "<div class='col-xs-12'>" +
            "<input id='address' type='text' class='form-control form input-lg'/>" +
            "</div>" +
            "";
        footerContent =
            "<div class='col-xs-12 col-md-4 col-md-offset-4 wam-not-padding'>" +
            "<hr><button type='button' class='btn btn-danger btn-lg btn-block ' " +
            "onclick=\"$('#modal').modal('hide');sendEmail();\">" +
            "Отправить" +
            "</button>" +
            "</div>" +
            "<div class='col-xs-12 col-md-4 wam-not-padding'>" +
            "<button type='button' class='btn btn-primary btn-lg btn-block ' data-dismiss='modal'>" +
            "Отмена" +
            "</button>" +
            "</div>";
    } else if (type == 'message') {
        bodyContent =
            "<div class='col-xs-12'>" +
            "<h4><strong>" + message + "</strong></h4>" +
            "<hr></div>" +
            "";
        footerContent =
            "<div class='col-xs-12 col-md-6 col-md-offset-6 wam-not-padding'>" +
            "<button type='button' class='btn btn-primary btn-lg btn-block ' data-dismiss='modal' onclick=\"location.href='/'\">" +
            "Закрыть" +
            "</button>" +
            "</div>";
    }
    $('#modalBody').append(bodyContent);
    $('#modalFooter').append(footerContent);

    $('#modal').modal('show');
}
/**
 * Функция прорисовки круговой диаграммы
 * @param data данные в формате "наименование=сумма,наименование=сумма"
 * @param elementId id элемента, где следует нарисовать диаграмму
 */
function drawChartOfTypes(data, elementId) {
    //var parent = $('#' + elementId).parent();
    //parent.empty();


    var pieChartCanvas = $('#' + elementId).get(0).getContext('2d');
    var pieChart = new Chart(pieChartCanvas);
    var PieData = [];

    var colors = ['#5cb85c', '#f0ad4e', '#f0ad4e', '#d9534f'];
    var curNumStyle = -1;

    var array = data.split(',');
    var count = 0;
    var totalSum = 0
    array.forEach(function (pair, index, array) {
        var arrayPair = pair.split('=');
        var name = arrayPair[0];
        var sum = arrayPair[1];

        curNumStyle = curNumStyle < 4 ? curNumStyle + 1 : 0;

        PieData[count] =
        {
            value: sum,
            color: colors[curNumStyle],
            highlight: colors[curNumStyle],
            label: name
        };

        totalSum += sum;
        count++;
    });

    if (totalSum == 0) {
        $('#chartNaN').show();
        $('#typeChart').hide();
    } else {
        $('#chartNaN').hide();
        $('#typeChart').show();
        var pieOptions = {
            //Boolean - Whether we should show a stroke on each segment
            segmentShowStroke: true,
            //String - The colour of each segment stroke
            segmentStrokeColor: '#fff',
            //Number - The width of each segment stroke
            segmentStrokeWidth: 2,
            //Number - The percentage of the chart that we cut out of the middle
            percentageInnerCutout: 50, // This is 0 for Pie charts
            //Number - Amount of animation steps
            animationSteps: 100,
            //String - Animation easing effect
            animationEasing: 'easeOutBounce',
            //Boolean - Whether we animate the rotation of the Doughnut
            animateRotate: true,
            //Boolean - Whether we animate scaling the Doughnut from the centre
            animateScale: false,
            //Boolean - whether to make the chart responsive to window resizing
            responsive: true,
            // Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
            maintainAspectRatio: true,
            //String - A legend template
            legendTemplate: '<ul class=\"\<\%=name.toLowerCase()%>-legend\">\<\% ' +
            'for (var i=0; i<segments.length; i++){' +
            '%><li><span style=\"background-color:\<\%=segments[i].fillColor%>\"></span>' +
            '<\%if(segments[i].label){' +
            '%>\<\%=segments[i].label%>\<\%}%></li>\<\%}%></ul>'
        };
        pieChart.Doughnut(PieData, pieOptions);
    }
}