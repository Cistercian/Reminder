<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<header class="content wam-radius">
    <nav class="navbar navbar-default wam-radius" role="navigation">
        <!-- Название компании и кнопка, которая отображается для мобильных устройств группируются для лучшего отображения при свертывании -->
        <div class="row">
            <div class="col-xs-12 col-md-10">
                <img src="/resources/img/logoName.png" class="img-responsive wam-top-radius wam-margin-left-1 wam-margin-top-1" alt="">
            </div>
            <div class="col-xs-12 col-md-2">
                <img src="/resources/img/logoPict.png" class="img-responsive wam-top-radius wam-margin-top-1" alt="">
            </div>
        </div>
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
        </div>

        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav navbar-left">
                <li><a href="/">Главная</a></li>
                <li><a href="/data">Данные</a></li>

            </ul>

        </div><!-- /.navbar-collapse -->
        <div id="alerts" class="wam-ontop col-sm-6 col-sm-offset-6">
        </div>
    </nav>
</header>

<!-- Modal Panel -->
<div id="modal" class="modal  " tabindex="-1" role="dialog" aria-labelledby="modalHeader"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content wam-radius">
            <div id="modalHeader" class="modal-header ">
                <div  class="modal-title">
                </div>
            </div>
            <div id="modalBody" class="modal-body">
                Loading data...
            </div>
            <div id='modalFooter' class="modal-footer wam-margin-top-1">
                <div class="col-xs-12 col-md-4 col-md-offset-8">
                    <button type="button" class="btn-primary btn-lg btn-block" data-dismiss="modal"></button>
                </div>
            </div>
        </div>
        <!-- /.modal-content -->
    </div>
    <!-- /.modal-dialog -->
</div>
