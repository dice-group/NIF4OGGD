<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="i18n.messages" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
   <head>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
      <link rel="stylesheet" type="text/css" href="css/main.css">
      <link rel="stylesheet" href="css/jquery-ui.css" />
      <link rel="stylesheet" type="text/css" href="css/style.css">
      <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />

      <script type="text/javascript" src="js/jquery-1.9.1.js"></script>
      <script src="js/jquery-ui.js"></script>
      <script type="text/javascript" src="js/main.js"></script>
      <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false"></script>

      <title><fmt:message key="main.title"/></title>

   </head>
   
   <body>

     <div id="loading-container" class="loading-container"></div>

	 <ul id="menu_header">
		<li><a href="javascript:showAbout();"><fmt:message key="main.about"/></a></li>
		<li>|</li>
	 	<li><a href="javascript:showHowTo();"><fmt:message key="main.howto"/></a></li>
	 	<li>|</li>
	 	<li><a href="javascript:showHelp();"><fmt:message key="main.help"/></a></li>
	 	<li>|</li>
	 	<li><a href="javascript:showContact();"><fmt:message key="main.contact"/></a></li>
      </ul>	
      
      <div class="clear"></div>    

      <div id="header">
		<input id="search_content" /> <span id="search_image" onclick="updateMap();"> <img src="images/search.png" alt="Search"/> </span>
      </div>



	   <div id="logos">
			<a href="http://www.dbpedia.org" target="_blank"><img src="images/dbpedia.png" class="image_space" border="0" alt='DBpedia.org'/></a>
			<a href="http://www.aksw.org" target="_blank"><img src="images/aksw.png" class="image_space" border="0"  alt='aksw.org'/></a>      
	   </div>

     <div class="clear"></div>  
     <div id="map-canvas">
	 </div>

    <div class="group" id="gov_docs">
        <article id="docs">
            <hgroup>
              <h1>Documents</h1>
            </hgroup>
            <div id="docs-container">
                <div class="nano">
                    <div class="content">
                            <ul id="docs_list">
                            </ul>
                    </div>
                </div>
            </div>
        </article>
        <article id="governament_document">
               <hgroup>
                  <h1 id="title_document"></h1>
              </hgroup>
              <div id="governament_document-container">
                <div class="nano">
                  <div class="content" id="document_text">
                  </div>
                </div>
              </div>
        </article>
    </div>

    <div id="about-modal" title="<fmt:message key="main.about"/>">
     <p><fmt:message key="main.about.message"/></p>
    </div>

    <div id="how-to-modal" title="<fmt:message key="main.howto"/>">
     <p><fmt:message key="main.howto.message"/></p>
    </div>

    <div id="help-modal" title="<fmt:message key="main.help"/>">
     <p><fmt:message key="main.help.message"/></p>
    </div>

    <div id="contact-modal" title="<fmt:message key="main.contact"/>">
     <p><fmt:message key="main.contact.message"/></p>
    </div>



   </body>
</html>
