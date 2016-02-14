<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="com.mehinovic.kalah.game.Kalah"%>
<%@page import="com.mehinovic.kalah.servlet.KalahServlet"%>
<%@page import="com.mehinovic.kalah.game.KalahConfiguration"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title>Kalah Game</title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="stylesheet" href="resources/css/vendor/bootstrap.min.css">
        <link rel="stylesheet" href="resources/css/vendor/bootstrap-theme.min.css">
        <link rel="stylesheet" href="resources/css/main.css">
        <!--[if lt IE 9]>
            <script src="//html5shiv.googlecode.com/svn/trunk/html5.js"></script>
            <script>window.html5 || document.write('<script src="resources/js/vendor/html5shiv.js"><\/script>')</script>
        <![endif]-->
    </head>
    <%
        final Kalah game = (Kalah) session.getAttribute(KalahServlet.GAME_KEY);
        final boolean hasGame = game != null;
        final KalahConfiguration gameConfig = new KalahConfiguration();
    %>
    <body>
        <div class="container">
            <div class="row">
                <div class="col-xs-10 col-xs-offset-1">
                    <div id="game">
                        <div class="row-fluid">
                            <div id="kalahp1" class="kalah col-xs-2"></div>
                            <div class="col-xs-8">
                                <div id="playerOnePits" class="row"></div>
                                <div id="playerTwoPits" class="row"></div>
                            </div>
                            <div id="kalahp2" class="kalah col-xs-2"></div>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-10 col-xs-offset-1">
                    <div id="output">
                        <div class="alert alert-info" role="alert">
                            Please click on the pit which stones you want to move
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
            	<div class="col-xs-10 col-xs-offset-1">
            		<button id="resetGame" type="button" class="btn btn-primary">New Game</button>
            	</div>
            </div>
        </div>
        <script>var hasGame = <%= hasGame %>, pSize = <%= gameConfig.getPits() %>, sSize = <%= gameConfig.getStones() %>;</script>
        <script src="resources/js/vendor/prototype.js"></script>
        <script src="resources/js/main.js"></script>
    </body>
</html>