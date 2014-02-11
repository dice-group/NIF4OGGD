var avgLatitude=0;
var avgLongitude=0;
var ZOOM_MAP=17;

/**
* Initialize the screen
*/
$(document).ready(function() {

    initGovDocsDivs()

    initSearch();

    plotSimpleMarker(new google.maps.LatLng(51, 9), 6);

    initMessageDIVS();

});

function getDocs(street){

    var loadingContainer = $('#loading-container', this.el);

        $('*').css( 'cursor', 'wait' );

        loadingContainer.show();

        $.ajax({
               url: "rest/nifoggd",
               dataType: "json",
               data: {
                    featureClass: "P",
                    style: "full",
                    maxRows: 16,
                    text: street,
                    docs: "getDocs"
               },

               success: function( data ) {
                    $.map( data, function( item ) {

                       var selectList = $('#docs_list');
                           selectList.empty();
                       $("#document_text").html("");
                       $("#title_document").html("");

                        if(item.documents!="")
                          {
                              $.each(item.documents, function( index, content ) {
                                  $("#governament_document-container").data("D"+index, content);
                                  var title = content.split("###")[0];

                                  if(title!=""){
                                      selectList.append(' <li><a href="Javascript:doLoadData(\'' + "D"+index +'\');">' + title.substr(0,title.length>35?35:length) + "..." + '</a></li>');
                                  }
                              });
                          }
                    });


                  loadingContainer.hide();
                  $('*').css( 'cursor', 'default' );

               }});

}

/**
* Initialize Search
*/
function initSearch(street){

    $( "#search_content").focus().autocomplete({
 	    source: function( request, response ) {
			$.ajax({
			       url: "rest/nifoggd",
			       dataType: "json",
			       data: {
					    featureClass: "P",
					    style: "full",
					    maxRows: 16,
					    text: request.term.split(",")[0].trim()
				   },

			       success: function( data ) {
			        	response( $.map( data, function( item ) {
                         $("#gov_docs").hide();
                          var selectList = $('#docs_list');
                              selectList.empty();
                              $("#document_text").html("");
                              $("#title_document").html("");

            			return {label: item.name + " , " + item.city ,
			            		value: item.latitude + "," + item.longitude
			                   }
			            }));
				   }
			        });

        },

	    minLength: 1,

		select: function( event, ui ) {

            var array = ui.item.value.split(',');
            var latitude = array[0].split('#');
            var longitude = array[1].split('#');

            selectedCoordinates = [];
            var len = latitude.length==0?1:latitude.length;
            avgLatitude=0;
            avgLongitude=0;

           // for (var i = 0; i < len; i++) {
                avgLatitude  = parseFloat(latitude[0]);
                avgLongitude = parseFloat(longitude[0]);
           // }
            //avgLatitude = avgLatitude/len;
            //avgLongitude = avgLongitude/len;

            getDocs(ui.item.label);

            $("#gov_docs").show();

           updateMap();

           $( "#search_content" ).val( ui.item.label );

          return false;

		},
		open: function() {
			$( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
		},
		close: function() {
			$( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
		}
	});
}

/**
* Initialize divs
*/
function initGovDocsDivs()
{
    $("#gov_docs" ).css({'top': 100, 'left' : 100, 'position': 'absolute'});
    $("#gov_docs").hide();
}

/**
* Load text on text area
*/
function doLoadData(index){

    var text = $("#governament_document-container").data(index)
    var title = text.split("###")[0]

    $("#title_document").html(title);
    $("#document_text").html( text.split("###").join("<br>"));
}

/**
* Plot a Map using Google Maps API
*
* @centerArea: "centralized" point
* @maxZoom : zoom
* @flightPlanCoordinates: street coordinates
*/
function plotSimpleMarker(centerArea, maxZoom)
{
   var mapOptions = {
       zoom: maxZoom,
       center: centerArea,
       mapTypeId: google.maps.MapTypeId.ROADMAP
   };

   var map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

    if(avgLatitude!=0 && avgLongitude !=0)
    {
       var myLatlng = new google.maps.LatLng(avgLatitude,avgLongitude);

        var marker = new google.maps.Marker({
             position: myLatlng,
             map: map,
             title: ''
         });
     }
}

/**
* Refresh a Google Map using
*/
function updateMap()
{
    if(avgLatitude!=0 && avgLongitude !=0)
         plotSimpleMarker(new google.maps.LatLng(avgLatitude, avgLongitude), ZOOM_MAP);

    $( "#search_content").trigger('keydown');


}

function initMessageDIVS(){
     $("#contact-modal").hide();
     $("#about-modal").hide();
     $("#how-to-modal").hide();
      $("#help-modal").hide();
}

function showAbout(){
 $("#about-modal").dialog({
    height: 200,
    width: 450,
    modal: true
    });
}

function showHowTo(){
 $("#how-to-modal").dialog({
    height: 200,
    width: 450,
    modal: true
    });
}

function showHelp(){
 $("#help-modal").dialog({
     height: 200,
     width: 450,
    modal: true
    });
}

function showContact(){
 $("#contact-modal").dialog({
    height: 200,
    width: 450,
    modal: true
    });
}