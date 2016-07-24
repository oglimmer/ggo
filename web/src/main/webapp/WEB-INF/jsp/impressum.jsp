<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<stripes:layout-render name="/WEB-INF/jsp/common.jsp">
  <stripes:layout-component name="head">
	 <!-- Main jumbotron for a primary marketing message or call to action -->
	 <div class="jumbotron">
	   <div class="container">
	     <h2>Impressum / Datenschutz / Privacy</h2>
	   </div>
	 </div>
  </stripes:layout-component>
  <stripes:layout-component name="center">

	<h2>Impressum</h2>
	
	<p>
		<img src="images/impressum.png" />
	</p>

	<h3>
	Hinweis zur Speicherung, Verarbeitung und Übermittlung personenbezogener Daten
	</h3>
	<div>
	Im Rahmen dieser Webseite werden Daten gespeichert, verarbeitet.
	</div>
	
	<h3> 
	Welche Daten werden übermittelt?
	</h3>
	<div>
	Wird ein Spiel (echtzeit, email oder tutorial) gestartet wird ein Cookie auf deinem Computer gespeichert. Dieser Cookie
	enthält die Spieler-ID, damit du beim nächsten mal das Spiel fortsetzen kannst.
	Du kannst außerdem, freiwillig, deine Email-Adresse hinterlassen, so dass wir dich, sollte ein anderer ein Spiel starten,
	benachrichtigen können.
	</div>
	
	<h3> 
	Wer hat Einsicht in deine Dokumente und Daten?
	</h3>
	<div> 
	Die Email-Adresse kann nur vom Administrator/Inhaber (Oliver Zimpasser) dieser Seite eingesehen werden. 
	</div>
	 
	<h3>
	An wen werden deine Daten weitergegeben?
	</h3>
	<div>
	Deine Daten werden innerhalb dieser Webseite gespeichert. An andere Dritte erfolgt keine Weitergabe.
	</div> 
	
	<h3>
	Wie lange erfolgt die Speicherung?
	</h3>
	<div>
	Deine Daten (email adresse) werden auf diesem Server solange gespeichert, bis du zur löschen aufforderst. Dazu kannst du
	einen link in der Benachrichtigungsemail anklicken oder eine Email an <a href='&#109;&#97;i&#108;to&#58;m&#97;&#37;69l&#64;&#111;%&#54;C%69&#37;6&#66;urt%2&#69;de'>m&#97;il&#64;&#111;lik&#117;rt&#46;d&#101;</a>  
	</div>

 </stripes:layout-component>
</stripes:layout-render>