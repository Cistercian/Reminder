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
            "<h4><strong>Укажите почтовый адрес получателя письма</strong></h4>" +
            "</div>" +
            "<div class='col-xs-12'>" +
            "<input id='address' type='text' class='form-control form input-lg'/>" +
            "</div>" +
            "";
        footerContent =
            "<div class='col-xs-12 col-md-4 col-md-offset-4 wam-not-padding'>" +
            "<button type='button' class='btn btn-danger btn-lg btn-block ' " +
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
            "</div>" +
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