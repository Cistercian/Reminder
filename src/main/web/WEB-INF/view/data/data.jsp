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
                'columnRisk': $('#columnRisk').val(),
                'columnRating': $('#columnRating').val(),
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

<div class="content container-fluid wam-radius wam-min-height-0 wam-panel-border">
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
                            <h4 class="wam-margin-bottom-0 wam-margin-top-0">Настройки структуры загружаемых файлов (номера используемых столбцов):</h4>
                        </div>
                        <div class="panel-body ">
                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h5>Наименование клиента</h5>
                                </div>
                                <div class="col-xs-12 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="columnName" type="number" class="form-control wam-text-size-2" value="${columnName}"/>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h5>Даты заполнения анкеты</h5>
                                </div>
                                <div class="col-xs-12 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="columnCreateDate" type="number" class="form-control wam-text-size-2" value="${columnCreateDate}"/>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h5>Дата обновления анкеты</h5>
                                </div>
                                <div class="col-xs-12 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="columnUpdateDate" type="number" class="form-control wam-text-size-2" value="${columnUpdateDate}"/>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h5>Степень риска</h5>
                                </div>
                                <div class="col-xs-12 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="columnRisk" type="number" class="form-control wam-text-size-2" value="${columnRisk}"/>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h5>Оценка риска</h5>
                                </div>
                                <div class="col-xs-12 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="columnRating" type="number" class="form-control wam-text-size-2" value="${columnRating}"/>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-xs-12 col-md-4 wam-margin-top-1 wam-not-padding-xs">
                                    <h5>Код подразделения</h5>
                                </div>
                                <div class="col-xs-12 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="columnBranchCode" type="number" class="form-control wam-text-size-2" value="${columnBranchCode}"/>
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
                                <div class="col-xs-12 col-md-3 wam-margin-top-1 wam-not-padding-xs">
                                    <h5>Адрес сервера</h5>
                                </div>
                                <div class="col-xs-2 col-md-1">
                                    <p class="wam-margin-top-1">
                                        <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                             alt="" data-toggle="collapse" data-target="#helpSmtpHost">
                                    </p>
                                </div>
                                <div class="col-xs-12 col-md-8 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="smtpHost" type="text" class="form-control wam-text-size-2" value="${smtpHost}"/>
                                    </div>
                                </div>
                                <div id="helpSmtpHost" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                    <div class="panel panel-default">
                                        <div class="wam-not-padding panel-body">
                                            <div class="col-xs-12 col-md-12">
                                                <p class="wam-margin-top-2 text-justify">
                                                    Здесь необходимо указать адрес почтового сервера. Принципиально обмен должен осущесвтляться по протоколу smpt.
                                                </p>
                                                <p>
                                                    Например: mskmail.msk.russb.org
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xs-12 col-md-3 wam-margin-top-1 wam-not-padding-xs">
                                    <h5>Номер порта</h5>
                                </div>
                                <div class="col-xs-2 col-md-1">
                                    <p class="wam-margin-top-1">
                                        <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                             alt="" data-toggle="collapse" data-target="#helpSmtpPort">
                                    </p>
                                </div>
                                <div class="col-xs-12 col-md-2 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="smtpPort" type="number" class="form-control wam-text-size-2" value="${smtpPort}"/>
                                    </div>
                                </div>
                                <div id="helpSmtpPort" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                    <div class="panel panel-default">
                                        <div class="wam-not-padding panel-body">
                                            <div class="col-xs-12 col-md-12">
                                                <p class="wam-margin-top-2 text-justify">
                                                    Здесь необходимо указать порт для подключения к почтовому серверу.
                                                </p>
                                                <p>
                                                    Например стандартный порт для протокола smtp: 25
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xs-12 col-md-3 wam-margin-top-1 wam-not-padding-xs">
                                    <h5>Имя пользователя для авторизации</h5>
                                </div>
                                <div class="col-xs-2 col-md-1">
                                    <p class="wam-margin-top-1">
                                        <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                             alt="" data-toggle="collapse" data-target="#helpSmtpLogin">
                                    </p>
                                </div>
                                <div class="col-xs-12 col-md-6 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="smtpLogin" type="text" class="form-control wam-text-size-2" value="${smtpLogin}"/>
                                    </div>
                                </div>
                                <div id="helpSmtpLogin" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                    <div class="panel panel-default">
                                        <div class="wam-not-padding panel-body">
                                            <div class="col-xs-12 col-md-12">
                                                <p class="wam-margin-top-2 text-justify">
                                                    Здесь необходимо указать имя пользователя, который имеет право подключаться к серверу по указанному порту. Обратите внимание на то, что часто политика безопасности ограничивает
                                                    список таких учетных записей, поэтому их необходимо согласовывать с администраторами почтового сервера. Не забывайте указывать домен
                                                </p>
                                                <p>
                                                    Например: E-BURG\scaneburg
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xs-12 col-md-3 wam-margin-top-1 wam-not-padding-xs">
                                    <h5>Пароль для авторизации</h5>
                                </div>
                                <div class="col-xs-2 col-md-1">
                                    <p class="wam-margin-top-1">
                                        <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                             alt="" data-toggle="collapse" data-target="#helpSmtpPassword">
                                    </p>
                                </div>
                                <div class="col-xs-12 col-md-6 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="smtpPassword" type="password" class="form-control wam-text-size-2" value="${smtpPassword}"/>
                                    </div>
                                </div>
                                <div id="helpSmtpPassword" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                    <div class="panel panel-default">
                                        <div class="wam-not-padding panel-body">
                                            <div class="col-xs-12 col-md-12">
                                                <p class="wam-margin-top-2 text-justify">
                                                    Пароль для указанной выше учетной записи.
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xs-12 col-md-3 wam-margin-top-1 wam-not-padding-xs">
                                    <h5>Адрес отправителя</h5>
                                </div>
                                <div class="col-xs-2 col-md-1">
                                    <p class="wam-margin-top-1">
                                        <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                             alt="" data-toggle="collapse" data-target="#helpSmtpSender">
                                    </p>
                                </div>
                                <div class="col-xs-12 col-md-6 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="smtpSender" type="text" class="form-control wam-text-size-2" value="${smtpSender}"/>
                                    </div>
                                </div>
                                <div id="helpSmtpSender" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                    <div class="panel panel-default">
                                        <div class="wam-not-padding panel-body">
                                            <div class="col-xs-12 col-md-12">
                                                <p class="wam-margin-top-2 text-justify">
                                                    Здесь необходимо указать почтовый адрес отправителя, который будет отображаться в отправляемых письмах. Адрес должен быть реален, т.к. на сервере наверняка установлена проверка
                                                    соответствия имени пользователя, указанного выше, с существующей записью на почтовом сервере.
                                                </p>
                                                <p>
                                                    Например: scaneburg@rgsbank.ru
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-xs-12 col-md-3 wam-margin-top-1 wam-not-padding-xs">
                                    <h5>Тема письма</h5>
                                </div>
                                <div class="col-xs-2 col-md-1">
                                    <p class="wam-margin-top-1">
                                        <img src="/resources/img/help.png" class="img-responsive wam-top-radius center-block"
                                             alt="" data-toggle="collapse" data-target="#helpSmtpTitle">
                                    </p>
                                </div>
                                <div class="col-xs-12 col-md-8 wam-margin-top-1 wam-not-padding-xs">
                                    <div class="form-group">
                                        <input id="smtpTitle" type="text" class="form-control wam-text-size-2" value="${smtpTitle}"/>
                                    </div>
                                </div>
                                <div id="helpSmtpTitle" class="col-xs-12 col-md-12 wam-not-padding-xs collapse">
                                    <div class="panel panel-default">
                                        <div class="wam-not-padding panel-body">
                                            <div class="col-xs-12 col-md-12">
                                                <p class="wam-margin-top-2 text-justify">
                                                    Укажите тему, с которой будут отправляться письма.
                                                </p>
                                            </div>
                                        </div>
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
                                    onclick="location.href='/data'; return false;">
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