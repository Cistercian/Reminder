<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr">
<head>
    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reminder</title>

    <!-- css -->
    <spring:url value="/resources/css/bootstrap.min.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <spring:url value="/resources/css/style.css" var="css"/>
    <link rel="stylesheet" href="${css}">
    <link href="https://fonts.googleapis.com/css?family=Roboto+Slab" rel="stylesheet">

    <!-- js -->
    <spring:url value="/resources/js/jquery.js" var="js"/>
    <script src="${js}"></script>
    <spring:url value="/resources/js/bootstrap.min.js" var="js"/>
    <script src="${js}"></script>
    <!--waitingDialog-->
    <spring:url value="/resources/js/waitingDialog.js" var="js"/>
    <script src="${js}"></script>

    <!--custom functions-->
    <spring:url value="/resources/js/web.account.functions.js" var="js"/>
    <script src="${js}"></script>
</head>
<body>

<!-- навигационная панель и модальное окно -->
<jsp:include page="/WEB-INF/view/tags/nav-panel.jsp"></jsp:include>

<script language="javascript" type="text/javascript">
    $(document).ready(function () {
        if ($(response).val() != '') {
            displayMessage('message', $('#response').val());
        }
    });
    $(function () {
        $(document).on('change', ':file', function () {
            var input = $(this),
                    numFiles = input.get(0).files ? input.get(0).files.length : 1,
                    label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
            input.trigger('fileselect', [numFiles, label]);
        });
        $(document).ready(function () {
            $(':file').on('fileselect', function (event, numFiles, label) {
                var input = $(this).parents('.input-group').find(':text'),
                        log = numFiles > 1 ? /*numFiles + ' файлов выбрано'*/ label : label;
                if (input.length) {
                    input.val(log);
                }
            });
        });
    });

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
                    "<div class='col-xs-12 col-md-4 wam-not-padding'>" +
                    "<button type='button' class='btn btn-primary btn-lg btn-block ' data-dismiss='modal'>" +
                    "Закрыть" +
                    "</button>" +
                    "</div>";
        }
        $('#modalBody').append(bodyContent);
        $('#modalFooter').append(footerContent);

        $('#modal').modal('show');
    }
    function sendEmail(){
        $.ajax({
            url:  '/sendEmail',
            type: 'POST',
            data: {
                'address': $('#address').val()
            },
            dataType: 'json',
            beforeSend: function () {
                displayLoader();
            },
            success: function (data) {
                hideLoader();

                displayMessage('message', data.message);
            }
        });
    }
</script>

<div class="content container-fluid wam-radius wam-min-height-0">
    <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <textarea id="response" name="response" style="display: none;">${response}</textarea>
    <div class='row'>
        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-panel">
                <div class="panel-heading wam-page-title">
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0">Статистика и данные</h3>
                </div>
                <div class="panel-body">
                    <div class="row">
                        <div class="col-xs-12 col-md-6">
                            <p class='wam-margin-top-3 text-justify'>Текущее кол-во клиентов в БД:</p>
                        </div>
                        <div class="col-xs-12 col-md-6">
                            <p class='wam-margin-top-3 text-justify'><strong>${countTotal}</strong></p>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-xs-12 col-md-6">
                            <p class='text-justify'>Кол-во клиентов, по которым необходимо обновить анкету:</p>
                        </div>
                        <div class="col-xs-12 col-md-6">
                            <p class='text-justify'><strong>${countOverdue}</strong></p>
                        </div>
                    </div>

                    <form:form action="data/import" enctype="multipart/form-data" useToken="true">
                        <div class="row wam-margin-top-2">
                            <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                                <div class="input-group ">
                                    <label class="input-group-btn">
										<span class="btn btn-primary ">
											Выбрать файл&hellip;
                                            <input type="file" style="display: none;" multiple name="file">
										</span>
                                    </label>
                                    <input type="text" class="form-control" readonly>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12 col-md-6 wam-not-padding-xs">
                                <button type="submit" class="btn-danger btn-lg btn-block wam-btn-2">
                                    Импортировать
                                </button>
                            </div>
                        </div>
                    </form:form>
                </div>
            </div>

            <div class="panel panel-default wam-margin-panel">
                <div class="panel-heading wam-page-title">
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0">Уведомления</h3>
                </div>
                <div class="panel-body">
                    <div class="col-xs-12">
                        <p class="wam-margin-top-2">
							<span>
								Информацию о списке клиентов, анкеты которых подлежат пересмотру, Вы можете отправить письмом или ...
							</span>
                        </p>
                    </div>
                    <div class="col-xs-12 col-md-6">
                        <button type="submit" class="btn-primary btn-lg btn-block wam-btn-2"
                                onclick="displayMessage('sendEmail', ''); return false;">
                            Отправить почтовое уведомление
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>