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
    function saveSettings(){
        $.ajax({
            url:  '/data/settings/save',
            type: 'POST',
            data: {
                'columnName': $('#columnName').val(),
                'columnCreateDate': $('#columnCreateDate').val(),
                'columnUpdateDate': $('#columnUpdateDate').val(),
                'columnBranchCode': $('#columnBranchCode').val(),
                'smtpHost': $('#smtpHost').val(),
                'smtpPort': $('#smtpPort').val(),
                'smtpLogin': $('#smtpLogin').val(),
                'smtpPassword': $('#smtpPassword').val(),
                'smtpSender': $('#smtpSender').val(),
                'smtpTitle': $('#smtpTitle').val(),
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
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0">Настройки</h3>
                </div>
                <div class="panel-body ">
                    <div class="panel panel-default wam-margin-panel">
                        <div class="panel-heading wam-page-title">
                            <h4 class="wam-margin-bottom-0 wam-margin-top-0">Настройки структуры загружаемых файлов:</h4>
                        </div>
                        <div class="panel-body ">
                            <div class="col-xs-12">
                                <h4><strong>Номера используемых столбцов:</strong></h4>
                            </div>
                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h4>Наименование клиента</h4>
                                </div>
                                <div class="col-xs-12 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="columnName" type="number" class="form-control wam-text-size-1" value="${columnName}"/>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h4>Дата заведения клиента</h4>
                                </div>
                                <div class="col-xs-12 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="columnCreateDate" type="number" class="form-control wam-text-size-1" value="${columnCreateDate}"/>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h4>Дата обновления анкеты</h4>
                                </div>
                                <div class="col-xs-12 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="columnUpdateDate" type="number" class="form-control wam-text-size-1" value="${columnUpdateDate}"/>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h4>Код подразделения</h4>
                                </div>
                                <div class="col-xs-12 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="columnBranchCode" type="number" class="form-control wam-text-size-1" value="${columnBranchCode}"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="panel panel-default wam-margin-panel">
                        <div class="panel-heading wam-page-title">
                            <h4 class="wam-margin-bottom-0 wam-margin-top-0">Параметры подключения к почтовому серверу</h4>
                        </div>
                        <div class="panel-body ">
                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h4>Адрес сервера</h4>
                                </div>
                                <div class="col-xs-12 col-md-8 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="smtpHost" type="text" class="form-control wam-text-size-1" value="${smtpHost}"/>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h4>Номер порта</h4>
                                </div>
                                <div class="col-xs-12 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="smtpPort" type="text" class="form-control wam-text-size-1" value="${smtpPort}"/>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h4>Имя пользователя для авторизации</h4>
                                </div>
                                <div class="col-xs-12 col-md-6 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="smtpLogin" type="text" class="form-control wam-text-size-1" value="${smtpLogin}"/>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h4>Пароль для авторизации</h4>
                                </div>
                                <div class="col-xs-12 col-md-6 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="smtpPassword" type="password" class="form-control wam-text-size-1" value="${smtpPassword}"/>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h4>Адрес отправителя</h4>
                                </div>
                                <div class="col-xs-12 col-md-6 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="smtpSender" type="text" class="form-control wam-text-size-1" value="${smtpSender}"/>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h4>Тема письма</h4>
                                </div>
                                <div class="col-xs-12 col-md-8 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="smtpTitle" type="text" class="form-control wam-text-size-1" value="${smtpTitle}"/>
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-xs-12 col-md-6">
                            <button type="submit" class="btn-primary btn-lg btn-block wam-btn-2"
                                    onclick="saveSettings();">
                                Сохранить
                            </button>
                        </div>
                        <div class="col-xs-12 col-md-6">
                            <button type="submit" class="btn-default btn-lg btn-block wam-btn-2"
                                    onclick="location.href='/data/data'; return false;">
                                Отмена
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>