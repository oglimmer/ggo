<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<stripes:layout-definition>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8" />
	<title>GridGameOne</title>
	<link rel="stylesheet" href="css/styles.css" />	
	<link rel="stylesheet" href="webjars/bootstrap/3.3.6/css/bootstrap.min.css" />
	<link rel="stylesheet" href="webjars/bootstrap/3.3.6/css/bootstrap-theme.min.css" />

</head>
<body>

	<nav class="navbar navbar-inverse navbar-default">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">GridGameOne</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
			<ul class="nav navbar-nav navbar-right">
				<li><a href="Help.action" target="_blank">Instructions</a></li>
			</ul>
          <!-- 
          <form class="navbar-form navbar-right">
            <div class="form-group">
              <input type="text" placeholder="Email" class="form-control">
            </div>
            <div class="form-group">
              <input type="password" placeholder="Password" class="form-control">
            </div>
            <button type="submit" class="btn btn-success">Sign in</button>
          </form>
           -->
        </div><!--/.navbar-collapse -->
      </div>
    </nav>

    <stripes:layout-component name="head"/>

    <div class="container">
	  <stripes:layout-component name="center"/>

      <hr>

      <footer>
        <p>Created by <a href="http://oglimmer.de" target="_blank">oglimmer.de</a> - ${actionBean.longVersion } - <stripes:link beanclass="de.oglimmer.ggo.web.action.ImpressumActionBean">Impressum/Kontakt/Datenschutz&nbsp;</stripes:link> </p>
      </footer>
    </div> <!-- /container -->

</body>
</html>
</stripes:layout-definition>