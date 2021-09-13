<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

@font-face {
    font-family: "OpenSans";
    src: url(OpenSans-Regular.ttf) format("truetype");
}

html,body {
	margin: 0;
	padding: 0;
	height: 100%;
	width: 100%;
	background-color: #FFFFFF;
	overflow: hidden;
}

<%-- login --%>
.login-window {
	background-color: #FFFFFF;
}

.login-box-body {
	width: 660px;
	background-image: url(../images/login-box-bg.png);
	background-repeat: repeat-y;
	background-color: transparent;
	z-index: 1;
	padding: 0;
	margin: 0;
	text-align: center;
	padding-bottom: 0px;
}

.login-box-header {
	background-image: url(../images/login-box-header.png);
	background-color: transparent;
	z-index: 2;
	height: 54px;
	width: 660px;
}

.login-box-header-txt {
	color: black;
	font-weight: normal;
	font-size: 18px;
	position: relative;
	top: 30px;
}

.login-box-header-logo {
	padding-top: 10px;
	padding-bottom: 10px;
}

.login-box-footer {
	background-image: url(../images/login-box-footer.png);
	background-position: bottom right;
	background-attachment: local;
	background-repeat: repeat-y;
	z-index: 2;
	height: 90px;
	width: 660px;
}

.login-box-footer-pnl {
	width: 604px;
	margin-left: 10px;
	margin-right: 10px;
	padding-top: 10px;
}

.login-label {
	color: black;
	text-align: right;
	width: 40%;
}

.login-field {
	text-align: left;
	width: 55%;
}

.login-btn {
	height: 36px;
	width: 72px;
}

.login-east-panel, .login-west-panel {
	width: 350px;
	background-color: #F2F2F2;
	border-color: #F2F2F2;
	position: relative;
}

<%-- header --%>
.desktop-header-left {
	margin: 0;
	margin-left: 5px;
	margin-top: 3px;
}

.desktop-header-right {
	margin: 0;
	margin-top: 3px;
	padding-right: 5px;
}

.disableFilter img {
	opacity: 0.2;
	filter: progid : DXImageTransform . Microsoft . Alpha(opacity = 20);
	-moz-opacity: 0.2;
}

.toolbar {
	padding: 0px;
}

.toolbar-button img {
	width: 22px;
	height: 22px;
	padding: 0px 1px 0px 1px;
	border-style: solid;
	border-width: 1px;
	border-color: transparent;
}

.toolbar-button-large img {
	width: 44px;
	height: 44px;
	padding: 0px 1px 0px 1px;
	border-style: solid;
	border-width: 1px;
	border-color: transparent;
}

.embedded-toolbar-button img {
	width: 16px;
	height: 16px;
	padding: 0px 1px 0px 1px;
	border-style: solid;
	border-width: 1px;
	border-color: transparent;
}

.depressed img {
	border-style: inset;
	border-width: 1px;
	border-color: #9CBDFF;
	background-color: #C4DCFB;
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
	padding: 0px 1px 0px 1px;
}

.desktop-header {
	background-image: url(../images/header-bg.png);
	background-repeat: repeat-x;
	background-position: bottom left;
	background-color: white;
	width: 100%;
	height: 35px;
}

.desktop-header-font {
	font-family: "OpenSans", Verdana, Arial, Helvetica, sans-serif;
	font-size: 12px;
}

<%-- button --%>
.action-button {
	height: 32px;
	width: 48px;
}

.action-text-button {
	height: 32px;
	width: 80px;
}

.editor-button {
	width: 26px;
	padding: 5px;
	height: 25px;
}

.editor-button img {
	vertical-align: middle;
	text-align: center;
}

<%-- desktop --%>
div.wc-modal, div.wc-modal-none, div.wc-highlighted, div.wc-highlighted-none {
	background-color: white;
}

.desktop-tabpanel {
	margin: 0;
	padding: 0;
	border: 0;
	position: absolute;
}

.menu-search {
	background-color: #E0EAF7;
}

<%-- adwindow and form --%>
.adform-content-none {
	overflow: auto;
	position: absolute;
	width: 100%;
	margin: 3px;
}

.adwindow-status {
	background-color: #E0EAF7;
	height: 20px;
}

.adwindow-nav {
}

.adwindow-left-nav {
	border-right: 1px solid #7EAAC6;
	border-left: none;
}

.adwindow-right-nav {
	border-left: 1px solid #7EAAC6;
	border-right: none;
}

.adwindow-nav-content {
	background-color: #E0EAF7;
	height: 100%;
}

.adwindow-toolbar {
	border: 0px;
}

.adwindow-navbtn-dis, .adwindow-navbtn-sel, .adwindow-navbtn-uns {
	border: 0px;
	margin-top: 3px;
	padding-top: 2px;
	padding-bottom: 2px;
}

.adwindow-navbtn-sel {
	background-color: #9CBDFF;
	font-weight: bold;
	color: #274D5F;
	cursor: pointer;
	border-top: 2px solid #7EAAC6;
	border-bottom: 2px solid #7EAAC6;
}

.adwindow-left-navbtn-sel {
	border-left: 2px solid #7EAAC6;
	border-right: 2px solid #7EAAC6;
	text-align: right;
	-moz-border-radius-topleft: 5px;
	-moz-border-radius-bottomleft: 5px;
	-webkit-border-top-left-radius: 5px;
	-webkit-border-bottom-left-radius: 5px;
	background-color: #FFFFFF !important;
	background-image: url(../images/adtab-left-bg.png);
	background-repeat: repeat-y;
	background-position: top right;
}

.adwindow-right-navbtn-sel {
	border-right: 2px solid #7EAAC6;
	border-left: 2px solid #7EAAC6;
	text-align: left;
	-moz-border-radius-topright: 5px;
	-moz-border-radius-bottomright: 5px;
	-webkit-border-top-right-radius: 5px;
	-webkit-border-bottom-right-radius: 5px;
	background-color: white !important;
	background-image: none;
	background-repeat: repeat-y;
	background-position: top left;
}

.adwindow-navbtn-uns {
	background-color: #C4DCFB;
	font-weight: normal;
	color: #274D5F;
	cursor: pointer;
}

.adwindow-navbtn-dis {
	background-color: #C4DCFB;
}

.adwindow-navbtn-uns, .adwindow-navbtn-dis {
	border-top: 1px solid #CCCCCC;
	border-bottom: 1px solid #CCCCCC;
}

.adwindow-left-navbtn-uns, .adwindow-left-navbtn-dis {
	border-left: 1px solid #CCCCCC;
	border-right: 1px solid #CCCCCC;
	text-align: right;
	-moz-border-radius-topleft: 5px;
	-moz-border-radius-bottomleft: 5px;
	-webkit-border-top-left-radius: 5px;
	-webkit-border-bottom-left-radius: 5px;
}

.adwindow-right-navbtn-uns, .adwindow-right-navbtn-dis {
	border-right: 1px solid #CCCCCC;
	border-left: 1px solid #CCCCCC;
	text-align: left;
	-moz-border-radius-topright: 5px;
	-moz-border-radius-bottomright: 5px;
	-webkit-border-top-right-radius: 5px;
	-webkit-border-bottom-right-radius: 5px;
}

<%-- ad tab --%>
.adtab-body {
	position: absolute;
	margin: 0;
	padding: 0;
	width: 100%;
	height: 100%;
	border: none;
}

.adtab-content {
	margin: 0;
	padding: 0;
	border: none;
	overflow: auto;
	width: 100%;
	height: 100%;
	position: absolute;
}

.adtab-grid-panel {
	position: absolute;
	overflow: hidden;
	width: 100%;
	height: 100%;
}

.adtab-grid {
	width: 100%;
	position: absolute;
}

.adtab-tabpanels {
	width: 80%;
	border-top: 1px solid #9CBDFF;
	border-bottom: 1px solid #9CBDFF;
	border-left: 2px solid #9CBDFF;
	border-right: 2px solid #9CBDFF;
}

<%-- status bar --%>
.status {
	width: 100%;
	height: 20px;
}

.status-db {
	padding-top: 0;
	pdding-bottom: 0;
	padding-left: 5px;
	padding-right: 5px;
	cursor: pointer;
	width: 100%;
	height: 100%;
	margin: 0;
	border-left: solid 1px #9CBDFF;
}

.status-info {
	padding-right: 10px;
	border-left: solid 1px #9CBDFF;
}

.status-border {
	border: solid 1px #9CBDFF;
}

.form-button {
	width: 99%;
}

<%-- Combobox --%>
.z-combobox-disd {
	color: black !important; cursor: default !important; opacity: 1; -moz-opacity: 1; -khtml-opacity: 1; filter: alpha(opacity=100);
}

.z-combobox-disd * {
	color: black !important; cursor: default !important;
}

.z-combobox-text-disd {
	background-color: #ECEAE4 !important;
}

<%-- Button --%>
.z-button-disd {
	color: black; cursor: default; opacity: .6; -moz-opacity: .6; -khtml-opacity: .6; filter: alpha(opacity=60);
}

<%-- highlight focus form element --%>
input:focus, textarea:focus, .z-combobox-inp:focus, z-datebox-inp:focus {
	border: 1px solid #0000ff;
	background: #e3f5ff;
}

.mandatory-decorator-text {
	text-decoration: none; font-size: xx-small; vertical-align: top; color:red;
}
<%-- menu tree cell --%>
div.z-tree-body td.menu-tree-cell {
	cursor: pointer;
	padding: 0 2px;
   	font-size: ${fontSizeM};
   	font-weight: normal;
   	overflow: visible;
}

div.menu-tree-cell-cnt {
	border: 0; margin: 0; padding: 0;
	font-family: ${fontFamilyC};
	font-size: ${fontSizeM}; font-weight: normal;
    white-space:nowrap
}

td.menu-tree-cell-disd * {
	color: #C5CACB !important; cursor: default!important;
}

td.menu-tree-cell-disd a:visited, td.menu-tree-cell-disd a:hover {
	text-decoration: none !important;
	cursor: default !important;;
	border-color: #D0DEF0 !important;
}

div.z-dottree-body td.menu-tree-cell {
	cursor: pointer; padding: 0 2px;
	font-size: ${fontSizeM}; font-weight: normal; overflow: visible;
}

div.z-filetree-body td.menu-tree-cell {
	cursor: pointer; padding: 0 2px;
	font-size: ${fontSizeM}; font-weight: normal; overflow: visible;
}

div.z-vfiletree-body td.menu-tree-cell {
	cursor: pointer; padding: 0 2px;
	font-size: ${fontSizeM}; font-weight: normal; overflow: visible;
}

.z-tabs.cnt {
	background-color: #FFFFFF;
	color: #FFFFFF;
}

td.z-button-tl td.z-button-tm td.z-button-tr td.z-button-bm td.z-button-br td.z-button-cl td.z-button-cr {
	background-color: transparent;
	color: transparent;
}

.z-button {
    background-image: none;
}


.z-button .z-button-cl {
    background-image: none;
    background-color: #2b4d5f
}

.z-button .z-button-cr {
    background-image: none;
    background-color: #2b4d5f
}

.z-button .z-button-tl {
    background-image: none;
    background-color: #2b4d5f
}

.z-button .z-button-tm {
    background-image: none;
    background-color: #2b4d5f
}

.z-button .z-button-tr {
    background-image: none;
    background-color: #2b4d5f
}

.z-button .z-button-bl {
    background-image: none;
    background-color: #2b4d5f
}

.z-button .z-button-bm {
    background-image: none;
    background-color: #2b4d5f
}

.z-button .z-button-br {
    background-image: none;
    background-color: #2b4d5f
}

.z-button .z-button-cm {
	background-image: none;
    background-color: #2b4d5f;
    color: white;
    font-weight: bold;
}

.z-tabs-scroll {
	border: none;
	padding-bottom: 0px;
}

.z-tabs-scroll .z-tabs-cnt {
    background-image: none;
    background-color: white;
}

.z-toolbar {
	background: transparent;
	background-image: none;
}

.z-tab-hl {
	background: white;
	background-image: none;
	padding-left: 2px;
}

.z-tab-hr, .z-tab-hm {
	background: #49819f;
	background-image: none;
}

.z-tab .z-tab-hl:hover .z-tab-text, .z-tab .z-tab-hl .z-tab-text {
    color: white;
}

.z-textbox {
	background-color: white;
	border: 1px solid #E9E9E9;
	border-radius: 4px;
	width: 98%
}

.z-textbox:focus {
	border: 1px solid black;
	border-radius: 4px;
}

.z-combobox-inp {
	background: white;
	background-color: white;
	border: 1px solid #E9E9E9;
	border-radius: 4px;
}

.z-combobox-inp:focus {
	border: 1px solid black;
	border-radius: 4px;
}

.z-decimalbox {
	background-color: white;
	border: 1px solid #E9E9E9;
	border-radius: 4px;
}

.z-decimalbox:focus {
	border: 1px solid black;
	border-radius: 4px;
}

.z-datebox-inp {
	background: none;
	background-color: white;
	border: 1px solid #E9E9E9;
	border-radius: 4px;

}

.z-datebox-inp:focus {
	border: 1px solid black;
	border-radius: 4px;
}

.adwindow-right-navbtn-sel {
	background-color: white;
}

.adwindow-nav-content {
	background-color: white;
}

td.z-group-inner {
	border-top: none;
	border-bottom: none;
	padding: 0px;
}

.z-group-inner .z-group-cnt span, .z-group-inner .z-group-cnt {
	color: black;
	background: #e3e3e3;
	font-weight: normal;
}

.z-north, .z-south, .z-west, .z-center, .z-east {
	border: 1px solid white;
}

div.z-grid {
	background: white;
}

.z-east-splt, .z-west-splt, .z-north-splt, .z-south-splt {
	background-image: none;
	background: #c3c3c3;	
}

.z-west-header, .z-center-header, .z-east-header, .z-north-header, .z-south-header {
	background-image: none;
	background: #e3e3e3;
	border-bottom: 1px solid white;
}

.z-panel-noborder .z-panel-top.z-panel-top-noborder .z-toolbar {
	border-bottom: 1px solid white;
}

div.z-tree {
	background: white;
}

.z-toolbar a, .z-toolbar a:visited, .z-toolbar a {
	border: 1px solid white;
	background: white;
}

.z-border-layout {
	background-color: white;
	font-family: "OpenSans", Verdana, Arial, Helvetica, sans-serif;
}

div.z-grid-header th.z-column, div.z-grid-header th.z-auxheader {
	background-color: #2b4d5f;
	border-color: #2b4d5f;
	color: white;
	border: 2px solid #2b4d5f; 
}

tr.z-row td.z-row-inner {
	border-left: none;
	border-right: none;
}

td.z-row-inner, td.z-group-foot-inner {
	padding: 3px;
}

.z-textbox, .z-decimalbox, .z-intbox, .z-longbox, .z-doublebox {
	background-image: none;
	background: white;

}

.z-textbox:focus {
	background: #e3f5ff;
}

.z-textbox-readonly {
	color: gray;
}


.z-datebox-readonly {
	color: gray;
}

.z-decimalbox-readonly {
	color: gray;
}

.z-east-colpsd, .z-west-colpsd, .z-south-colpsd, .z-north-colpsd {
	border: none;
	background-color: #e3e3e3
}

.z-combobox .z-combobox-img {
	background-image: url(../images/combobtn.gif);
	border-bottom: 1px solid #e9e9e9;
}


.z-combobox-focus .z-combobox-img {
	border-bottom: 1px solid #e9e9e9;
}

.desktop-header-right {
	margin-top: 0px;
}

.z-menu-popup {
	background: white;
	background-image: none;
	border: 1px solid #e9e9e9;
}

.z-border-layout-icon {
	background-image: url(../images/borderlayout-btn.gif);
}

.z-panel-header {
	background: #e9e9e9;
	background-image: none;
	border: none;
	color: black;
	font-weight: normal;
}

.z-panel-children {
	border: none;
}

.z-toolbar {
	border-color: white;
}

.z-toolbar-button {
	color: black;
	text-decoration: none;
}

.z-panel-icon {
	background-image: url(../images/btn.gif);
}

.z-window-modal-header, .z-window-popup-header, .z-window-highlighted-header, .z-window-overlapped-header, .z-window-embedded-header { 
	background-color: #2b4d5f;
}

.z-window-embedded-tr, .z-window-modal-tr, .z-window-highlighted-tr, .z-window-overlapped-tr, .z-window-popup-tr {
	background-color: #2b4d5f;
	background: #2b4d5f;
	background-image: none;
}

.z-window-embedded-hr, .z-window-modal-hr, .z-window-highlighted-hr, .z-window-overlapped-hr, .z-window-popup-hr {
	background-color: #2b4d5f;
	background: #2b4d5f;
	background-image: none;
}

.z-window-embedded-hl, .z-window-modal-hl, .z-window-highlighted-hl, .z-window-overlapped-hl, .z-window-popup-hl {
	background-color: #2b4d5f;
	background: #2b4d5f;
	background-image: none;
}

.z-window-modal-cl, .z-window-highlighted-cl, .z-window-overlapped-cl {
	background-color: #2b4d5f;
	background: #2b4d5f;
	background-image: none;
}

.z-window-modal-cr, .z-window-highlighted-cr, .z-window-overlapped-cr {
	background-color: #2b4d5f;
	background: #2b4d5f;
	background-image: none;
}

.z-window-modal-br, .z-window-highlighted-br, .z-window-overlapped-br {
	background-color: #2b4d5f;
	background: #2b4d5f;
	background-image: none;
}

.z-window-modal-icon, .z-window-highlighted-icon, .z-window-overlapped-icon {
	background-image: url(../images/btn.gif);
}

.z-listbox {
	background: white;
	background-color: white;
	border: 1px solid #E9E9E9;
	border-radius: 4px;
}

.z-list-header-cnt {
	background-color: #2b4d5f;
	color: white;
}

.z-list-cell-cnt {
	background-color: white;
}

.z-list-header {
	background-color: #2b4d5f;
	border: 1px solid #2b4d5f;
} 

.z-calendar-calday td, .z-datebox-calday td {
    border: 1px dotted lightgray;
    padding: 1px 3px;
}

.z-calendar-calyear td, .z-datebox-calyear td {
	background: #2b4d5f;
	color: white;
}

.z-datebox-pp {
	padding: 0px;
	border: 2px solid #2b4d5f; 
}

div.z-listbox-body .z-list-cell, div.z-listbox-footer .z-list-footer {
	background: white;
}

.z-datebox .z-datebox-img {
	border-bottom: 1px solid #E9E9E9; 
	background-image: url(../images/datebtn.gif);
}


span.z-tree-root-open, span.z-tree-tee-open, span.z-tree-last-open, span.z-tree-root-close, span.z-tree-tee-close, span.z-tree-last-close {
	background-image: url(../images/toggle.gif);
}

.z-tab-accordion-tr {
	background-image: none;
	background: white;
}

.z-tab-accordion-hm {
	background-image: none;
	background: #2b4d5f;
}

.z-tab-accordion-hl {
	background-image: none;
	background: #2b4d5f;
}

.z-tab-accordion-hr {
	background-image: none;
	background: #2b4d5f;
}

.z-tab-accordion-text {
	color: white;
}

div.z-grid-header th.z-column, div.z-grid-header th.z-auxheader:hover {
    background-image: none;
}

div.z-grid-header .z-column-sort div.z-column-cnt:hover {
    background-image: none;
}

.z-column-btn {
	background-image: url(../images/hd-btn.png);
}

.z-group-img {
	background-image: url(../images/toggle.gif);
}