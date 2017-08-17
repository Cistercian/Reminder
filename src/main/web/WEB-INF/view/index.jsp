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

        $.ajax({
            url: '/getStats',
            type: "GET",
            dataType: 'json',
            success: function (data) {
                var tableData = new Array();
                data.forEach(function (data, index, stats) {
                    var entity = new Array();

                    entity[0] = data.count;
                    entity[1] = data.branchCode;

                    tableData.push(entity);
                });

                $('#bodyTable').append(
                        "<table id='stats' class='table table-striped table-bordered table-text wam-margin-top-2' cellspacing='0' " +
                        "width='100%'>" +
                        "</table>"
                );

                var table = $('#stats').DataTable({
                    responsive: true,
                    "bLengthChange": false,
                    language: {
                        "processing": "Подождите...",
                        "search": "Поиск:",
                        "lengthMenu": "Показать _MENU_ записей",
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
                        { title: "Количество просроченных анкет" },
                        { title: "Код подразделения" }
                    ],
                    "sort": true,
                    "order": [[0, "DESC"]],
                });

                $('#stats_filter').empty();
                $('#stats_filter').append(
                        "<div class='col-xs-2 col-md-4 wam-padding-left-0 wam-padding-right-0'>" +
                        "<h5>Поиск: </h5>" +
                        "</div>" +
                        "<div class='col-xs-10 col-md-8 wam-padding-left-0 wam-padding-right-0'>" +
                        "<input id='searchDataTable' type='text' class='form-control form' placeholder='' aria-controls='stats'>" +
                        "</div>"
                );
                $('#searchDataTable').on( 'keyup', function () {
                    table.search( this.value ).draw();
                });
            }
        });
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
                                <button type="submit" class="btn-danger btn-lg btn-block wam-btn-2"
                                        onclick="history.pushState({}, null, '/');">
                                    Импортировать
                                </button>
                            </div>
                        </div>
                    </form:form>
                </div>
            </div>

            <div class="panel panel-default wam-margin-panel">
                <div class="panel-heading wam-page-title">
                    <h3 class="wam-margin-bottom-0 wam-margin-top-0">Отчеты</h3>
                </div>
                <div class="panel-body">
                    <div id="bodyTable" class="panel-body">
                    </div>

                    <div class="col-xs-12">
                        <p class="wam-margin-top-2">
							<span>
								Информацию о клиентах, анкеты которых подлежат пересмотру, Вы можете отправить письмом или
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

</body>
</html>