<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>PLANETS: Workflow Online Design Tool</title>
<meta name="keywords" content="Planets, Digital Preservation, Digital Library, Archive, Europe, National Library, National Archive, European Commission, FP6" />
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<link rel="stylesheet" href="../css/screen_planets.nonavi.css" type="text/css" media="screen" />
<link rel="stylesheet" href="../css/screen_print.css" type="text/css" media="print" />
<link rel="stylesheet" href="../css/basic.css" type="text/css" media="print" />
<!--<style type="text/css" media="print">@import "/incs/print.css";</style>-->
<!--<style type="text/css" media="screen">@import "/incs/screen.css";</style>-->
<link rel="shortcut icon" href="http://www.planets.arts.gla.ac.uk/graphics/planets.ico" />
<link href="/rss/planets_news.xml" rel="alternate" type="application/rss+xml" title="Planets News" />
<script>
  function setSelectedMenuStyle() {
    if (document.location.href.indexOf('home.jsp') > -1)
        document.getElementById('home.jsp').className = 'menu_sel';
    else     
        document.getElementById('home.jsp').className = 'menu';

    if (document.location.href.indexOf('sendRequest.jsp') > -1)
        document.getElementById('sendRequest.jsp').className = 'menu_sel';
    else     
        document.getElementById('sendRequest.jsp').className = 'menu';
        
    if (document.location.href.indexOf('checkWSI.jsp') > -1)
        document.getElementById('checkWSI.jsp').className = 'menu_sel';
    else     
        document.getElementById('checkWSI.jsp').className = 'menu';                

    if ((document.location.href.indexOf('deployService.jsp') > -1) && (document.location.href.indexOf('undeployService.jsp') == -1))
        document.getElementById('deployService.jsp').className = 'menu_sel';
    else     
        document.getElementById('deployService.jsp').className = 'menu';                

    if (document.location.href.indexOf('undeployService.jsp') > -1)
        document.getElementById('undeployService.jsp').className = 'menu_sel';
    else     
        document.getElementById('undeployService.jsp').className = 'menu';                        
  }  
</script>    
</head>
<body style="font-size:1.0em;" onLoad="setSelectedMenuStyle()">
<div id="wrapper"><a name="top"></a> <span class="hide"><a href="#content">Skip Navigation / Jump to Content</a></span>

  <div id="header">
    <div class="logo"><a href="/sample/faces/home.jsp"><img style="border: 0px solid ; " src="../imgs/Planets_Logo.png" alt="Planets: Home" title="Planets: Home" /></a></div>
    <span class="sitetitle">Planets - IF/5 Online Workflow Design Tool</span> </div>
  <div id="headernav">
    <div class="menu">
      <ul><li><a href="home.jsp" title="Home" id="home.jsp">Home</a></li><li><a href="loadWF.jsp" id="loadWF.jsp" title="Load WS">Load WF</a></li><li><a href="chooseWS.jsp" id="chooseWS.jsp" title="Choose WS">Choose WS</a></li><li><a href="deployWF.jsp" id="deployWF.jsp" title="Deploy WF">Deploy</a></li><li><a href="undeployWF.jsp" id="undeployWF.jsp" title="undeploy WF">Undeploy</a></li><li><a href="executeWF.jsp" id="executeWF.jsp" title="execute WF">Execute</a></li></ul>
    </div>
  </div>

  <!-- All Navigation Stuff Here -->
  <!--<div id="navbar">
    <ul class="primary">
	<li><a class="navbar" href="/about/" title="Find out more about Planets">&raquo; About Planets</a></li><li><a class="navbar" href="/events/" title="Upcoming and previous events">&raquo; Events</a></li><li><a class="navbar" href="/publications/" title="Planets publications and materials">&raquo; Publications</a></li><li><a class="navbar" href="/newsletters/" title="Planets Newsletters">&raquo; Newsletters</a></li><li><a class="navbar" href="/partners/" title="Planets Partners and Consortium">&raquo; Partners</a></li></ul>


    <p class="istlogo"><a target="_blank" href="http://www.cordis.lu/ist/" rel="external"><img src="../imgs/ist_logo_small.jpg" alt="Information Society Technologies" title="Information Society Technologies" /></a></p>
  </div>-->
  <!-- End of Header: Content to begin -->
  <!-- Then all content here -->
  