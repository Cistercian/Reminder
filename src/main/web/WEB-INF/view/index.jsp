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
    <spring:url value="/resources/css/dataTables.bootstrap.css" var="css"/>
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
    <!--DataTables-->
    <spring:url value="/resources/js/jquery.dataTables.min.js" var="js"/>
    <script src="${js}"></script>
    <!--Диаграмма-->
    <spring:url value="/resources/js/Chart.min.js" var="js"/>
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
        $("#tabs li:eq(0) a").tab('show');

        if ($(response).val() != '') {
            displayMessage('message', $('#response').val());
        }

        drawChartOfTypes('Кол-во обработанных клиентов=${countTotal},Количество ошибок=${countOverdue}', 'chartTotal')

        $.ajax({
            url: '/getStats?type=all',
            type: "GET",
            dataType: 'json',
            success: function (data) {
                //данные для стилей прогресс баров
                var styles = ['success', 'info', 'warning', 'danger'];
                var curNumStyle = -1;
                var maxCount = 0;
                //массив для datatables
                var tableData = new Array();
                jQuery.each(data, function(index, data){
                    /*                data.forEach(function (data, index, stats) {  IE8...*/
                    var entity = new Array();

                    entity[0] = data.branchCode;
                    entity[1] = data.count;

                    tableData.push(entity);

                    //рисуем прогресс бары
                    maxCount = maxCount == 0 ? entity[1] : maxCount;
                    normalCount = entity[1] * 100 / maxCount;
                    curNumStyle = curNumStyle < 4 ? curNumStyle + 1 : 0;

                    $('#bars').append(
                            "<li class='list-unstyled'>" +
                            "<div>" +
                            "<h5><strong>Наименование подразделения " + entity[0] + "</strong>" +
                            "<span class='pull-right text-muted' value='" + entity[1] + "'>" + entity[1] + " шт.</span></h5>" +
                            "<div class='progress progress-striped active'> " +
                            "<div class='progress-bar progress-bar-" + styles[curNumStyle] + "' role='progressbar' " +
                            "aria-valuenow='" + entity[1] + "' aria-valuemin='0' aria-valuemax='100' " +
                            "style='width: " + normalCount + "%' value='" + entity[0] + "'>" +
                            "<span class='sr-only'>" + entity[1] + "</span>" +
                            "</div>" +
                            "</div>" +
                            "</div>" +
                            "</li>"
                    );
                });

                $('#bodyTable').append(
                        "<table id='stats' class='table table-striped table-bordered table-text wam-margin-top-2' cellspacing='0' " +
                        "width='100%'>" +
                        "</table>"
                );

                var table = $('#stats').DataTable({
                    responsive: true,
                    "bLengthChange": true,
                    language: {
                        "processing": "Подождите...",
                        "search": "Поиск:",
                        "lengthMenu": "Показать по _MENU_ записей",
                        "info": "Записи с _START_ до _END_ (Всего записей: _TOTAL_).",
                        "infoEmpty": "Записи с 0 до 0 из 0 записей",
                        "infoFiltered": "(отфильтровано из _MAX_ записей)",
                        "infoPostFix": "",
                        "loadingRecords": "Загрузка записей...",
                        "zeroRecords": "Записи отсутствуют.",
                        "emptyTable": "В таблице отсутствуют данные",
                        "paginate": {
                            "first": "Первая",
                            "previous": "Предыдущая",
                            "next": "Следующая",
                            "last": "Последняя"
                        },
                        "aria": {
                            "sortAscending": ": активировать для сортировки столбца по возрастанию",
                            "sortDescending": ": активировать для сортировки столбца по убыванию"
                        }
                    },
                    data: tableData,
                    columns: [
                        { title: "Код подразделения" },
                        { title: "Количество просроченных анкет" }
                    ],
                    "sort": true,
                    "order": [[1, "DESC"]]
                });

                $('#stats_filter').empty();
            }
        });
    });

    function drawBars(url){
        $.ajax({
            url: url,
            type: "GET",
            dataType: 'json',
            success: function (data) {
                //данные для стилей прогресс баров
                var styles = ['success', 'info', 'warning', 'danger'];
                var curNumStyle = -1;
                var maxCount = 0;

                $('#bars').empty();
                jQuery.each(data, function(index, data){
                    var entity = new Array();

                    entity[0] = data.branchCode;
                    entity[1] = data.count;

                    //рисуем прогресс бары
                    maxCount = maxCount == 0 ? entity[1] : maxCount;
                    normalCount = entity[1] * 100 / maxCount;
                    curNumStyle = curNumStyle < 4 ? curNumStyle + 1 : 0;

                    $('#bars').append(
                            "<li class='list-unstyled'>" +
                            "<div>" +
                            "<h5><strong>Наименование подразделения " + entity[0] + "</strong>" +
                            "<span class='pull-right text-muted' value='" + entity[1] + "'>" + entity[1] + " шт.</span></h5>" +
                            "<div class='progress progress-striped active'> " +
                            "<div class='progress-bar progress-bar-" + styles[curNumStyle] + "' role='progressbar' " +
                            "aria-valuenow='" + entity[1] + "' aria-valuemin='0' aria-valuemax='100' " +
                            "style='width: " + normalCount + "%' value='" + entity[0] + "'>" +
                            "<span class='sr-only'>" + entity[1] + "</span>" +
                            "</div>" +
                            "</div>" +
                            "</div>" +
                            "</li>"
                    );
                });
            }
        })
    }
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

<div class="content container-fluid wam-radius wam-min-height-0 wam-panel-border">
    <input id="_csrf_token" type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <textarea id="response" name="response" style="display: none;">${response}</textarea>
    <div class='row'>
        <div class="container-fluid wam-not-padding-xs">
            <div class="panel panel-default wam-margin-panel">
                <div class="panel-heading wam-page-title">
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0">Исходные данные</h3>
                </div>
                <div class="panel-body">
                    <div class="row">
                        <div class="col-xs-12 col-md-6">
                            <div class="col-xs-12 col-md-11">
                                <p class='wam-margin-top-3 text-justify'><span class="glyphicon glyphicon-stop wam-color-income"></span><span> Текущее кол-во клиентов в БД:</span></p>
                            </div>
                            <div class="col-xs-12 col-md-1">
                                <p class='wam-margin-top-3 text-justify'><strong>${countTotal}</strong></p>
                            </div>
                            <div class="col-xs-12 col-md-11">
                                <p class='text-justify'><span class="glyphicon glyphicon-stop wam-color-expense"></span><span> Кол-во ошибок:</span></p>
                            </div>
                            <div class="col-xs-12 col-md-1">
                                <p class='text-justify'><strong>${countOverdue}</strong></p>
                            </div>

                            <form:form action="data/import" enctype="multipart/form-data" useToken="true">
                                <div class="row wam-margin-top-2">
                                    <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                                        <p class='text-justify wam-margin-top-2'><strong>Импорт данных из ЦФТ</strong></p>
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
                                    <div class="col-xs-12 col-md-12 wam-not-padding-xs">
                                        <button type="submit" class="btn-danger btn-lg btn-block wam-btn-2"
                                                onclick="displayLoader();history.pushState({}, null, '/');">
                                            Импортировать
                                        </button>
                                    </div>
                                </div>
                            </form:form>
                        </div>
                        <div class="col-xs-12 col-md-6">
                            <canvas id="chartTotal"></canvas>
                        </div>
                    </div>


                </div>
            </div>

            <div class="panel panel-default wam-margin-panel">
                <div class="panel-heading wam-page-title">
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0">Отчеты</h3>
                </div>
                <div class="panel-body">

                    <ul id="tabs" class="nav nav-tabs">
                        <li><a data-toggle="tab" href="#panelTable">Ошибки итого</a></li>
                        <li><a data-toggle="tab" href="#panelBars" onclick="drawBars('/getStats?type=all')">Проц.соотношение</a></li>
                        <li><a data-toggle="tab" href="#panelBars" onclick="drawBars('/getStats?type=overdue')">Просроченные анкеты</a></li>
                        <li><a data-toggle="tab" href="#panelBars" onclick="drawBars('/getStats?type=risk')">Пустые степени риска</a></li>
                        <li><a data-toggle="tab" href="#panelBars" onclick="drawBars('/getStats?type=rating')">Ошибки в оценке риска</a></li>
                    </ul>
                    <div class="tab-content">
                        <div id="panelTable" class="tab-pane fade in active">
                            <div id="bodyTable" class="panel-body"></div>
                        </div>
                        <div id="panelBars" class="tab-pane fade in active">
                            <div id="bars" class="panel-body"></div>
                        </div>

                        <div class="col-xs-12">
                            <hr>
                            <p class="wam-margin-top-2">
							<span>
								Информацию о клиентах, карточки которых содержат ошибки, Вы можете отправить письмом или
								загрузить в виде файла.
							</span>
                            </p>
                        </div>
                        <div class="col-xs-12 col-md-6">
                            <button type="submit" class="btn-primary btn-lg btn-block wam-btn-2"
                                    onclick="displayMessage('sendEmail', ''); return false;">
                                Отправить почтой
                            </button>
                        </div>
                        <div class="col-xs-12 col-md-6">
                            <button type="submit" class="btn-default btn-lg btn-block wam-btn-2"
                                    onclick="location.href='/data/overdue'; return false;">
                                Выгрузить файл
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