<%@ page contentType="text/css;charset=UTF-8" %>
<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c" %>

@import url("../../default/css/theme.css.dsp");

@font-face {
    font-family: "OpenSans";
    src: url("../../default/css/OpenSans-Regular.ttf") format("truetype");
}

:root {
    --ly-font-sans: "Segoe UI Variable", "OpenSans", "Segoe UI", "Helvetica Neue", Arial, sans-serif;
    --ly-bg: #f6f3ee;
    --ly-surface: #fffdfa;
    --ly-surface-muted: #f3ede4;
    --ly-border: #d9d1c4;
    --ly-border-strong: #c2b59f;
    --ly-text: #14191d;
    --ly-text-muted: #69757d;
    --ly-primary: #162027;
    --ly-primary-strong: #0d1419;
    --ly-primary-soft: #e8edf0;
    --ly-accent: #efb026;
    --ly-accent-strong: #cc7f08;
    --ly-accent-soft: #fff1d3;
    --ly-danger: #b54747;
    --ly-shadow-sm: 0 10px 24px rgba(20, 25, 29, 0.08);
    --ly-shadow-md: 0 22px 44px rgba(20, 25, 29, 0.16);
    --ly-radius-sm: 8px;
    --ly-radius-md: 14px;
    --ly-radius-lg: 20px;
}

html,
body {
    background:
        radial-gradient(circle at top left, #d7e8f4 0, transparent 28%),
        radial-gradient(circle at bottom right, #d9efe8 0, transparent 24%),
        linear-gradient(180deg, #f8fbfd 0%, #eef4f9 100%);
    color: var(--ly-text);
    font-family: var(--ly-font-sans);
}

.login-shell,
.login-shell-center,
.login-shell-stage {
    background: transparent;
}

.login-shell-stage {
    padding: 48px 20px;
}

.login-shell-window {
    width: auto;
}

.login-shell-panel,
.login-shell-panel-role {
    max-width: 720px;
}

.login-window {
    background: transparent;
}

.login-east-panel,
.login-west-panel,
.login-north-panel,
.login-south-panel {
    background: transparent;
    border: none;
}

.login-box-header,
.login-box-body,
.login-box-footer {
    width: 720px;
}

.login-box-header {
    height: auto;
    padding: 28px 36px 12px;
    background: linear-gradient(135deg, rgba(15, 95, 143, 0.92), rgba(11, 69, 105, 0.96));
    border-radius: var(--ly-radius-lg) var(--ly-radius-lg) 0 0;
    box-shadow: var(--ly-shadow-md);
}

.login-box-header-txt {
    position: static;
    display: inline-block;
    margin-top: 18px;
    color: #f8fbfd;
    font-size: 28px;
    font-weight: bold;
    letter-spacing: 0.02em;
}

.login-box-body {
    background: var(--ly-surface);
    border-left: 1px solid rgba(15, 95, 143, 0.08);
    border-right: 1px solid rgba(15, 95, 143, 0.08);
    box-shadow: var(--ly-shadow-md);
    padding: 18px 36px 8px;
}

.login-box-header-logo {
    padding: 0 0 10px;
}

.login-box-header-logo img {
    max-height: 54px;
}

.login-form-grid {
    border-collapse: separate;
}

.login-role-grid {
    border-collapse: separate !important;
    border-spacing: 0 8px !important;
}

.login-role-grid td.login-field,
.login-role-grid td.login-label {
    padding-top: 0 !important;
    padding-bottom: 6px !important;
}

.login-feedback-row td {
    padding-top: 12px;
}

.login-feedback-cell {
    width: 100%;
}

.login-feedback-text {
    display: block;
    color: var(--ly-danger);
    text-align: left;
    font-size: 12px;
    font-weight: bold;
}

.login-box-footer {
    height: auto;
    background: var(--ly-surface);
    border: 1px solid rgba(15, 95, 143, 0.08);
    border-top: none;
    border-radius: 0 0 var(--ly-radius-lg) var(--ly-radius-lg);
    box-shadow: var(--ly-shadow-md);
    padding: 0 36px 28px;
}

.login-box-footer-pnl {
    width: auto;
    margin: 0;
    padding-top: 8px;
}

.login-label {
    width: 34%;
    color: var(--ly-text-muted);
    font-size: 13px;
    font-weight: bold;
    text-transform: uppercase;
    letter-spacing: 0.06em;
    vertical-align: middle;
}

.login-field {
    width: 66%;
}

.login-btn,
.z-button {
    min-width: 104px;
    height: 40px;
    border-radius: 999px;
    border: none;
    background-image: none;
}

.login-btn .z-button-cm,
.z-button .z-button-cm,
.z-button .z-button-cl,
.z-button .z-button-cr,
.z-button .z-button-tl,
.z-button .z-button-tm,
.z-button .z-button-tr,
.z-button .z-button-bl,
.z-button .z-button-bm,
.z-button .z-button-br {
    background: var(--ly-primary);
    background-image: none;
    color: #fff;
    font-weight: bold;
}

.login-btn:hover .z-button-cm,
.z-button:hover .z-button-cm,
.z-button:hover .z-button-cl,
.z-button:hover .z-button-cr,
.z-button:hover .z-button-tl,
.z-button:hover .z-button-tm,
.z-button:hover .z-button-tr,
.z-button:hover .z-button-bl,
.z-button:hover .z-button-bm,
.z-button:hover .z-button-br {
    background: var(--ly-primary-strong);
}

.desktop-header {
    height: 56px;
    background: linear-gradient(180deg, #fdfefe 0%, #eaf1f7 100%);
    border-bottom: 1px solid var(--ly-border);
    box-shadow: var(--ly-shadow-sm);
}

.desktop-header-font {
    font-family: var(--ly-font-sans);
    font-size: 13px;
    color: var(--ly-text);
}

.app-header {
    padding: 0 12px;
}

.app-header-layout {
    background: transparent;
}

.app-header-brand,
.app-header-user {
    background: transparent;
}

.app-header-logo {
    max-height: 34px;
}

.app-userpanel {
    gap: 2px;
}

.app-header-action {
    border-radius: 999px;
    padding: 4px 10px;
    color: var(--ly-primary-strong);
}

.app-header-action:hover {
    background: var(--ly-primary-soft);
}

.toolbar {
    padding: 4px 8px;
}

.toolbar-button img,
.toolbar-button-large img,
.embedded-toolbar-button img {
    border: none;
    border-radius: 10px;
    background: transparent;
    padding: 3px;
}

.toolbar-button img:hover,
.toolbar-button-large img:hover,
.embedded-toolbar-button img:hover,
.window-container-toolbar-btn img:hover {
    background: var(--ly-primary-soft);
}

.depressed img {
    border: none;
    background: var(--ly-primary-soft);
    border-radius: 10px;
    padding: 3px;
}

.window-container-toolbar {
    padding: 6px 10px 0;
    background: #f8fbfd;
    border-bottom: 1px solid var(--ly-border);
}

.window-container {
    background: transparent;
}

.window-container-toolbar-btn {
    border-radius: 999px;
    padding: 6px 10px;
    color: var(--ly-primary-strong);
}

.window-container-toolbar-btn.tab-list {
    background: var(--ly-primary-soft);
    font-weight: bold;
}

.z-tabs-scroll,
.z-tabs-scroll .z-tabs-cnt,
.z-tab-hl,
.z-tab-hr,
.z-tab-hm {
    background-image: none;
}

.z-tabs-scroll .z-tabs-cnt {
    background: #f8fbfd;
    border-bottom: 1px solid var(--ly-border);
}

.z-tab {
    margin-right: 4px;
}

.z-tab-hl,
.z-tab-hm,
.z-tab-hr {
    background: transparent;
}

.z-tab .z-tab-hl .z-tab-text,
.z-tab .z-tab-hl:hover .z-tab-text {
    color: var(--ly-text-muted);
}

.z-tab-seld .z-tab-hl,
.z-tab-seld .z-tab-hm,
.z-tab-seld .z-tab-hr {
    background: var(--ly-surface);
}

.z-tab-seld .z-tab-hl .z-tab-text,
.z-tab-seld .z-tab-hl:hover .z-tab-text {
    color: var(--ly-primary-strong);
    font-weight: bold;
}

.menu-search,
.adwindow-status,
.adwindow-nav-content {
    background: var(--ly-surface-muted);
}

.app-shell {
    background: transparent;
}

.app-shell-header-region {
    background: transparent;
    border: none;
}

.app-shell-menu-region {
    background: transparent;
    border-right: none;
    padding: 12px 0 12px 12px;
}

.app-shell-main-region {
    background: transparent;
    padding: 12px 12px 12px 10px;
}

.app-sidepanel,
.app-menu {
    height: 100%;
    background: var(--ly-surface);
    border: 1px solid var(--ly-border);
    border-radius: var(--ly-radius-lg);
    box-shadow: var(--ly-shadow-sm);
}

.app-menu-toolbar {
    background: transparent;
    padding: 10px 12px 6px;
}

.app-menu-searchbar {
    border-bottom: 1px solid var(--ly-border);
}

.app-menu-footer {
    border-top: 1px solid var(--ly-border);
    margin-top: 8px;
    padding-top: 8px;
    padding-bottom: 10px;
}

.app-menu-tree {
    background: transparent;
    border: none;
}

.app-menu .z-panel-children {
    padding: 0 8px 8px;
}

.app-menu .z-treecell-cnt,
.app-menu .z-treecell-text,
.app-menu .z-tree-ico,
.app-menu .z-treecell {
    color: var(--ly-text);
}

.app-menu .z-treecell-cnt {
    border-radius: 10px;
    padding: 4px 6px;
}

.app-menu .z-treecell-cnt:hover {
    background: var(--ly-primary-soft);
}

.adwindow-left-nav,
.adwindow-right-nav {
    border-color: var(--ly-border);
}

.adwindow-navbtn-sel {
    background: var(--ly-primary-soft);
    color: var(--ly-primary-strong);
    border-color: var(--ly-primary);
    border-radius: var(--ly-radius-sm);
}

.adwindow-navbtn-uns,
.adwindow-navbtn-dis {
    border-color: transparent;
    color: var(--ly-text-muted);
}

.z-textbox,
.z-decimalbox,
.z-intbox,
.z-longbox,
.z-doublebox,
.z-combobox-inp,
.z-datebox-inp,
.z-listbox {
    background: #fff;
    border: 1px solid var(--ly-border);
    border-radius: var(--ly-radius-sm);
    color: var(--ly-text);
}

.z-textbox,
.z-decimalbox,
.z-intbox,
.z-longbox,
.z-doublebox {
    padding: 6px 8px;
}

.z-textbox:focus,
.z-combobox-inp:focus,
.z-datebox-inp:focus,
input:focus,
textarea:focus {
    border: 1px solid var(--ly-primary);
    background: #fff;
    box-shadow: 0 0 0 3px rgba(15, 95, 143, 0.12);
}

.z-combobox .z-combobox-img,
.z-datebox .z-datebox-img {
    border-bottom: none;
    border-left: 1px solid var(--ly-border);
    background-color: #f7fafc;
}

div.z-grid,
div.z-tree,
.z-listbox,
.z-panel-children,
.z-panel-header,
.z-window-embedded-cnt,
.z-window-modal-cnt,
.z-window-highlighted-cnt,
.z-window-overlapped-cnt {
    background: var(--ly-surface);
}

div.z-grid-header th.z-column,
div.z-grid-header th.z-auxheader,
.z-list-header,
.z-list-header-cnt,
.z-calendar-calyear td,
.z-datebox-calyear td,
.z-tab-accordion-hm,
.z-tab-accordion-hl,
.z-tab-accordion-hr {
    background: var(--ly-primary);
    border-color: var(--ly-primary);
    color: #fff;
}

.z-grid,
.z-listbox,
.z-panel,
.z-window-embedded-cnt,
.z-window-modal-cnt,
.z-window-highlighted-cnt,
.z-window-overlapped-cnt {
    border-radius: var(--ly-radius-md);
}

.app-dashboard {
    padding: 8px;
}

.app-dashboard-card {
    border: 1px solid var(--ly-border);
    border-radius: var(--ly-radius-md);
    box-shadow: var(--ly-shadow-sm);
    overflow: hidden;
}

.app-dashboard-card .z-panel-header {
    background: #f8fbfd;
}

.z-panel-header {
    border-bottom: 1px solid var(--ly-border);
    color: var(--ly-text);
    font-weight: bold;
}

.z-toolbar,
.z-panel-noborder .z-panel-top.z-panel-top-noborder .z-toolbar {
    border-color: transparent;
    background: transparent;
}

.z-toolbar-button {
    color: var(--ly-primary-strong);
}

.z-window-modal-header,
.z-window-popup-header,
.z-window-highlighted-header,
.z-window-overlapped-header,
.z-window-embedded-header,
.z-window-embedded-tr,
.z-window-modal-tr,
.z-window-highlighted-tr,
.z-window-overlapped-tr,
.z-window-popup-tr,
.z-window-embedded-hr,
.z-window-modal-hr,
.z-window-highlighted-hr,
.z-window-overlapped-hr,
.z-window-popup-hr,
.z-window-embedded-hl,
.z-window-modal-hl,
.z-window-highlighted-hl,
.z-window-overlapped-hl,
.z-window-popup-hl {
    background: linear-gradient(135deg, var(--ly-primary), var(--ly-primary-strong));
    background-image: none;
}

.z-window-modal-icon,
.z-window-highlighted-icon,
.z-window-overlapped-icon,
.z-panel-icon,
.z-border-layout-icon,
.z-column-btn,
span.z-tree-root-open,
span.z-tree-tee-open,
span.z-tree-last-open,
span.z-tree-root-close,
span.z-tree-tee-close,
span.z-tree-last-close,
.z-group-img {
    filter: saturate(0.7);
}

div.z-listbox-body .z-list-cell,
div.z-listbox-footer .z-list-footer,
.z-list-cell-cnt {
    background: #fff;
}

.z-row-over,
.z-listitem-over {
    background: #f3f8fc;
}

.mandatory-decorator-text {
    color: var(--ly-danger);
    font-weight: bold;
}

.app-statusbar {
    border-top: 1px solid var(--ly-border);
    background: #f8fbfd;
}

.app-statusbar-main,
.app-statusbar-meta {
    color: var(--ly-text-muted);
}

.versionInfoBox {
    margin: 18px 0 0;
    padding: 12px 14px;
    background: var(--ly-surface-muted);
    color: var(--ly-text-muted);
    border: 1px solid var(--ly-border);
    border-radius: var(--ly-radius-sm);
    font-size: 11px;
    line-height: 1.4;
}

/* Stronger overrides for legacy ZK 3.6 widgets */
html,
body {
    background: #eff3f8;
}

.z-north,
.z-south,
.z-west,
.z-center,
.z-east,
.z-west-cnt,
.z-center-cnt,
.z-east-cnt,
.z-north-cnt,
.z-south-cnt {
    border: none !important;
    background: transparent;
}

.z-west-header,
.z-center-header,
.z-east-header,
.z-north-header,
.z-south-header {
    background: transparent;
    border: none;
    color: var(--ly-text-muted);
    font-weight: bold;
}

.z-east-splt,
.z-west-splt,
.z-north-splt,
.z-south-splt,
.z-east-colpsd,
.z-west-colpsd,
.z-south-colpsd,
.z-north-colpsd {
    background: transparent;
    border: none;
}

.app-shell-main-region .z-center-body,
.app-shell-menu-region .z-west-body {
    background: transparent;
}

.desktop-tabpanel,
.z-tabpanel,
.z-tabbox,
.z-panel,
div.z-grid,
div.z-tree,
.z-listbox,
.z-window-embedded-cnt,
.z-window-modal-cnt,
.z-window-highlighted-cnt,
.z-window-overlapped-cnt {
    background: var(--ly-surface);
    border: 1px solid var(--ly-border);
    border-radius: 14px;
    box-shadow: var(--ly-shadow-sm);
}

.desktop-tabpanel {
    overflow: hidden;
}

.desktop-tabpanel .z-tabpanel-cnt,
.z-tabpanel .z-tabpanel-cnt {
    padding: 0;
    background: var(--ly-surface);
}

.window-container-toolbar,
.z-panel-noborder .z-panel-top.z-panel-top-noborder .z-toolbar,
.adwindow-toolbar {
    background: #fff;
    border: none;
    border-bottom: 1px solid var(--ly-border);
    padding: 8px 10px;
}

.z-toolbar a,
.z-toolbar a:visited,
.z-toolbar a {
    border: none;
    background: transparent;
}

.toolbar-button img,
.toolbar-button-large img,
.embedded-toolbar-button img {
    width: 20px;
    height: 20px;
    padding: 4px;
    border-radius: 8px;
}

.z-tabs-scroll {
    padding: 6px 10px 0;
    background: transparent;
}

.z-tabs-scroll .z-tabs-cnt {
    background: transparent;
    border: none;
}

.z-tab {
    margin-right: 8px;
}

.z-tab-hl,
.z-tab-hm,
.z-tab-hr,
.z-tab-seld .z-tab-hl,
.z-tab-seld .z-tab-hm,
.z-tab-seld .z-tab-hr {
    background-image: none;
    border: none;
}

.z-tab .z-tab-hl .z-tab-text,
.z-tab .z-tab-hl:hover .z-tab-text {
    display: inline-block;
    padding: 8px 14px;
    border-radius: 999px;
    background: #f3f6fa;
    color: var(--ly-text-muted);
    font-size: 13px;
    font-weight: bold;
}

.z-tab-seld .z-tab-hl .z-tab-text,
.z-tab-seld .z-tab-hl:hover .z-tab-text {
    background: #ffffff;
    color: var(--ly-primary-strong);
    box-shadow: 0 1px 0 rgba(18, 38, 58, 0.04), 0 0 0 1px var(--ly-border);
}

.adwindow-status,
.adwindow-nav-content {
    background: transparent;
}

.adwindow-left-nav,
.adwindow-right-nav {
    background: #fbfcfe;
    border-color: var(--ly-border);
}

.adwindow-navbtn-dis,
.adwindow-navbtn-sel,
.adwindow-navbtn-uns {
    margin: 3px 6px;
    padding: 8px 12px;
    border-radius: 10px;
    border: 1px solid transparent;
    background: transparent;
    color: var(--ly-text-muted);
    font-weight: bold;
}

.adwindow-navbtn-uns:hover {
    background: #f5f8fc;
    color: var(--ly-text);
}

.adwindow-navbtn-sel {
    background: #edf5fb;
    color: var(--ly-primary-strong);
    border-color: #cfe0ef;
}

.adwindow-left-navbtn-sel,
.adwindow-left-navbtn-uns,
.adwindow-left-navbtn-dis,
.adwindow-right-navbtn-sel,
.adwindow-right-navbtn-uns,
.adwindow-right-navbtn-dis {
    border-left: 1px solid transparent !important;
    border-right: 1px solid transparent !important;
}

.adwindow-left-navbtn-sel,
.adwindow-right-navbtn-sel {
    border-left-color: #cfe0ef !important;
    border-right-color: #cfe0ef !important;
}

.z-textbox,
.z-decimalbox,
.z-intbox,
.z-longbox,
.z-doublebox,
.z-combobox-inp,
.z-datebox-inp {
    height: 32px;
    line-height: 20px;
    padding: 5px 10px;
    border: 1px solid #d4dde7;
    border-radius: 8px;
    background: #fff;
    color: var(--ly-text);
    font-size: 13px;
}

.z-textbox:focus,
.z-combobox-inp:focus,
.z-datebox-inp:focus,
input:focus,
textarea:focus {
    border-color: #7aa8c7;
    background: #fff;
    box-shadow: 0 0 0 3px rgba(15, 95, 143, 0.10);
}

.z-combobox .z-combobox-img,
.z-datebox .z-datebox-img {
    background-image: none;
    background-color: #f8fafc;
    border: 1px solid #d4dde7;
    border-left: none;
    width: 22px;
}

.z-combobox-focus .z-combobox-img,
.z-datebox-focus .z-datebox-img {
    border-color: #7aa8c7;
}

td.z-row-inner,
td.z-group-foot-inner {
    padding: 6px 10px;
    border-top: none;
    border-left: none;
    border-right: none;
}

tr.z-row td.z-row-inner {
    border-bottom: 1px solid #eef2f6;
}

td.z-group-inner {
    padding: 14px 10px 6px;
    border: none;
    background: transparent;
}

.z-group-inner .z-group-cnt,
.z-group-inner .z-group-cnt span {
    background: transparent;
    color: var(--ly-primary-strong);
    font-size: 12px;
    font-weight: bold;
    letter-spacing: 0.04em;
    text-transform: uppercase;
}

div.z-grid-header th.z-column,
div.z-grid-header th.z-auxheader,
.z-list-header,
.z-list-header-cnt,
.z-calendar-calyear td,
.z-datebox-calyear td {
    background: #f7fafc;
    color: var(--ly-text-muted);
    border: none;
    border-bottom: 1px solid var(--ly-border);
    font-weight: bold;
}

div.z-grid-header th.z-column,
div.z-grid-header th.z-auxheader:hover,
div.z-grid-header .z-column-sort div.z-column-cnt:hover {
    background-image: none;
}

.z-listbox {
    border: 1px solid var(--ly-border);
    background: #fff;
}

.z-list-header-cnt,
.z-list-cell-cnt {
    background: transparent;
}

div.z-listbox-body .z-list-cell,
div.z-listbox-footer .z-list-footer {
    background: #fff;
    border-color: #eef2f6;
}

.z-window-modal-header,
.z-window-popup-header,
.z-window-highlighted-header,
.z-window-overlapped-header,
.z-window-embedded-header {
    background: #fff;
    color: var(--ly-text);
    border-bottom: 1px solid var(--ly-border);
}

.z-window-embedded-tr,
.z-window-modal-tr,
.z-window-highlighted-tr,
.z-window-overlapped-tr,
.z-window-popup-tr,
.z-window-embedded-hr,
.z-window-modal-hr,
.z-window-highlighted-hr,
.z-window-overlapped-hr,
.z-window-popup-hr,
.z-window-embedded-hl,
.z-window-modal-hl,
.z-window-highlighted-hl,
.z-window-overlapped-hl,
.z-window-popup-hl,
.z-window-modal-cl,
.z-window-highlighted-cl,
.z-window-overlapped-cl,
.z-window-modal-cr,
.z-window-highlighted-cr,
.z-window-overlapped-cr,
.z-window-modal-br,
.z-window-highlighted-br,
.z-window-overlapped-br {
    background: #fff;
    background-image: none;
}

.z-menu-popup {
    background: #fff;
    border: 1px solid var(--ly-border);
    box-shadow: var(--ly-shadow-sm);
}

.app-menu .z-treecell-cnt {
    border-radius: 8px;
    padding: 4px 8px;
    font-size: 13px;
}

.app-menu .z-treecell-cnt:hover {
    background: #eef4fb;
}

.app-menu-expand-toggle {
    color: var(--ly-text-muted);
}

.app-dashboard-card .z-panel-header {
    background: #fff;
}

@media screen and (max-width: 900px) {
    .login-box-header,
    .login-box-body,
    .login-box-footer {
        width: 94vw;
    }

    .login-label,
    .login-field {
        display: block;
        width: 100%;
        text-align: left;
    }

    .app-shell-menu-region,
    .app-shell-main-region {
        padding: 8px;
    }
}

/* Libertya Next refinements */
html,
body {
    background:
        radial-gradient(circle at top left, rgba(239, 176, 38, 0.16) 0, transparent 24%),
        radial-gradient(circle at bottom right, rgba(22, 32, 39, 0.10) 0, transparent 30%),
        linear-gradient(180deg, #faf7f2 0%, #f2eee7 100%);
    color: var(--ly-text);
}

.desktop-header,
.app-header,
.app-header-layout {
    background: rgba(255, 253, 250, 0.96);
}

.desktop-header {
    height: 64px;
    border-bottom: 1px solid rgba(22, 32, 39, 0.08);
    box-shadow: 0 10px 28px rgba(20, 25, 29, 0.06);
}

.app-header {
    padding: 0 16px;
}

.app-header-logo {
    max-height: 42px;
}

.desktop-header-font,
.desktop-header-font a,
.desktop-header-font a:visited {
    color: var(--ly-text);
    font-family: var(--ly-font-sans);
    font-size: 13px;
}

.app-header-action {
    margin-left: 4px;
    padding: 6px 12px;
    border-radius: 999px;
    color: var(--ly-primary);
    font-weight: bold;
}

.app-header-action:hover {
    background: var(--ly-accent-soft);
    color: var(--ly-primary-strong);
}

.app-shell-menu-region {
    padding: 16px 0 16px 16px;
}

.app-shell-main-region {
    padding: 16px 16px 16px 12px;
}

.app-menu {
    background: linear-gradient(180deg, #212c34 0%, #131a1f 100%);
    border: none;
    box-shadow: 0 18px 42px rgba(10, 15, 19, 0.26);
}

.app-menu .z-panel-header,
.app-menu .z-panel-header-text,
.app-menu-toolbar,
.app-menu-footer,
.app-menu-expand-toggle,
.app-menu .z-label {
    color: rgba(255, 255, 255, 0.86);
}

.app-menu .z-panel-header {
    background: transparent;
    border-bottom: none;
    padding: 14px 14px 4px;
    font-size: 16px;
    letter-spacing: 0.02em;
}

.app-menu-toolbar {
    padding: 10px 14px 8px;
}

.app-menu-searchbar {
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.app-menu-footer {
    border-top: 1px solid rgba(255, 255, 255, 0.08);
    margin-top: 8px;
    padding-top: 10px;
}

.app-menu-searchbar .z-label,
.app-menu-searchbar label,
.app-menu-searchbar span {
    color: rgba(255, 255, 255, 0.68);
}

.app-menu-searchbar input,
.app-menu-searchbar .z-textbox,
.app-menu-searchbar .z-combobox-inp {
    height: 36px;
    border: 1px solid rgba(255, 255, 255, 0.08);
    border-radius: 12px;
    background: rgba(255, 255, 255, 0.08);
    color: #f4f6f7;
}

.app-menu-searchbar input:focus,
.app-menu-searchbar .z-textbox:focus,
.app-menu-searchbar .z-combobox-inp:focus {
    border-color: rgba(239, 176, 38, 0.65);
    box-shadow: 0 0 0 3px rgba(239, 176, 38, 0.16);
}

.app-menu .z-panel-children,
.app-menu .z-tree,
.app-menu-tree,
.app-menu .z-tree-body {
    background: transparent;
}

.app-menu .z-treecell,
.app-menu .z-treecell-cnt,
.app-menu .z-treecell-text,
.app-menu .z-tree-ico,
div.z-tree-body td.menu-tree-cell,
div.menu-tree-cell-cnt {
    color: rgba(255, 255, 255, 0.84);
}

.app-menu .z-treecell-cnt,
div.menu-tree-cell-cnt {
    display: block;
    margin: 1px 0;
    padding: 8px 10px;
    border-radius: 12px;
    font-size: 13px;
    transition: background-color 120ms ease, color 120ms ease, transform 120ms ease;
}

.app-menu .z-treecell-cnt:hover,
div.menu-tree-cell-cnt:hover {
    background: rgba(255, 255, 255, 0.08);
    color: #ffffff;
    transform: translateX(1px);
}

.app-menu .z-treerow-selected .z-treecell-cnt,
.app-menu .z-treerow-seld .z-treecell-cnt,
.app-menu tr.z-treeitem-seld .z-treecell-cnt,
.app-menu td.menu-tree-cell-seld div.menu-tree-cell-cnt {
    background: linear-gradient(135deg, rgba(239, 176, 38, 0.26), rgba(204, 127, 8, 0.18));
    color: #ffffff;
    box-shadow: inset 0 0 0 1px rgba(239, 176, 38, 0.22);
}

.app-menu span.z-tree-root-open,
.app-menu span.z-tree-tee-open,
.app-menu span.z-tree-last-open,
.app-menu span.z-tree-root-close,
.app-menu span.z-tree-tee-close,
.app-menu span.z-tree-last-close {
    filter: brightness(1.2) saturate(0.9);
}

.app-menu-expand-toggle {
    font-size: 12px;
}

.desktop-tabpanel,
.z-tabpanel,
.z-tabbox,
.z-panel,
div.z-grid,
div.z-tree,
.z-listbox,
.z-window-embedded-cnt,
.z-window-modal-cnt,
.z-window-highlighted-cnt,
.z-window-overlapped-cnt {
    background: rgba(255, 253, 250, 0.98);
    border: 1px solid rgba(22, 32, 39, 0.08);
}

.window-container-toolbar,
.adwindow-toolbar {
    min-height: 52px;
    padding: 10px 14px;
    background: rgba(255, 253, 250, 0.98);
    border-bottom: 1px solid rgba(22, 32, 39, 0.08);
}

.toolbar-button,
.toolbar-button-large,
.embedded-toolbar-button,
.window-container-toolbar-btn {
    display: inline-block;
    margin-right: 6px;
    vertical-align: middle;
}

.toolbar-button img,
.toolbar-button-large img,
.embedded-toolbar-button img,
.window-container-toolbar-btn img {
    width: 22px;
    height: 22px;
    padding: 7px;
    border-radius: 12px;
    background: #f5efe7;
    box-shadow: inset 0 0 0 1px rgba(22, 32, 39, 0.06);
}

.toolbar-button img:hover,
.toolbar-button-large img:hover,
.embedded-toolbar-button img:hover,
.window-container-toolbar-btn img:hover {
    background: var(--ly-accent-soft);
    box-shadow: inset 0 0 0 1px rgba(239, 176, 38, 0.35);
}

.depressed img {
    background: linear-gradient(135deg, rgba(239, 176, 38, 0.24), rgba(204, 127, 8, 0.18));
    box-shadow: inset 0 0 0 1px rgba(239, 176, 38, 0.38);
}

.window-container-toolbar-btn {
    min-height: 36px;
    border-radius: 999px;
    padding: 0 12px;
    color: var(--ly-primary);
    font-weight: bold;
}

.window-container-toolbar-btn.tab-list {
    background: var(--ly-primary);
    color: #fff;
    box-shadow: 0 10px 24px rgba(22, 32, 39, 0.18);
}

.z-tabs-scroll {
    padding: 10px 14px 0;
}

.z-tab {
    margin-right: 10px;
}

.z-tab .z-tab-hl .z-tab-text,
.z-tab .z-tab-hl:hover .z-tab-text {
    padding: 10px 16px;
    border-radius: 999px;
    background: #f4efe7;
    color: var(--ly-text-muted);
    box-shadow: inset 0 0 0 1px rgba(22, 32, 39, 0.05);
}

.z-tab-seld .z-tab-hl .z-tab-text,
.z-tab-seld .z-tab-hl:hover .z-tab-text {
    background: #ffffff;
    color: var(--ly-primary);
    box-shadow: 0 10px 24px rgba(20, 25, 29, 0.10), inset 0 0 0 1px rgba(239, 176, 38, 0.30);
}

.adwindow-left-nav,
.adwindow-right-nav {
    background: linear-gradient(180deg, #202a31 0%, #13191f 100%);
    border: none;
    border-radius: 18px;
    min-width: 220px !important;
    padding: 8px 6px;
    box-shadow: 0 18px 42px rgba(10, 15, 19, 0.20);
}

.adwindow-navbtn-dis,
.adwindow-navbtn-sel,
.adwindow-navbtn-uns {
    margin: 0 0 5px;
    padding: 8px 12px;
    border-radius: 14px;
    border: 1px solid transparent;
    background: transparent;
    color: rgba(255, 255, 255, 0.74);
    font-weight: bold;
    line-height: 1.15;
}

.adwindow-navbtn-uns:hover {
    background: rgba(255, 255, 255, 0.06);
    color: #fff;
}

.adwindow-navbtn-sel {
    background: linear-gradient(135deg, rgba(239, 176, 38, 0.26), rgba(204, 127, 8, 0.16));
    color: #fff;
    border-color: rgba(239, 176, 38, 0.34);
    box-shadow: inset 3px 0 0 var(--ly-accent);
}

.adwindow-navbtn-dis {
    color: rgba(255, 255, 255, 0.34);
}

td.editor-button {
    width: 34px !important;
    min-width: 34px !important;
    max-width: 34px !important;
    padding-left: 0 !important;
    padding-right: 0 !important;
    vertical-align: middle;
}

button.editor-button,
.editor-button.z-button {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    border-radius: 8px;
    overflow: hidden;
}

button.editor-button .z-button-cm,
button.editor-button .z-button-cl,
button.editor-button .z-button-cr,
button.editor-button .z-button-tl,
button.editor-button .z-button-tm,
button.editor-button .z-button-tr,
button.editor-button .z-button-bl,
button.editor-button .z-button-bm,
button.editor-button .z-button-br,
.editor-button.z-button .z-button-cm,
.editor-button.z-button .z-button-cl,
.editor-button.z-button .z-button-cr,
.editor-button.z-button .z-button-tl,
.editor-button.z-button .z-button-tm,
.editor-button.z-button .z-button-tr,
.editor-button.z-button .z-button-bl,
.editor-button.z-button .z-button-bm,
.editor-button.z-button .z-button-br {
    background: linear-gradient(180deg, #22303a 0%, #162027 100%);
    background-image: none;
    color: #fff;
}

button.editor-button:hover .z-button-cm,
button.editor-button:hover .z-button-cl,
button.editor-button:hover .z-button-cr,
button.editor-button:hover .z-button-tl,
button.editor-button:hover .z-button-tm,
button.editor-button:hover .z-button-tr,
button.editor-button:hover .z-button-bl,
button.editor-button:hover .z-button-bm,
button.editor-button:hover .z-button-br,
.editor-button.z-button:hover .z-button-cm,
.editor-button.z-button:hover .z-button-cl,
.editor-button.z-button:hover .z-button-cr,
.editor-button.z-button:hover .z-button-tl,
.editor-button.z-button:hover .z-button-tm,
.editor-button.z-button:hover .z-button-tr,
.editor-button.z-button:hover .z-button-bl,
.editor-button.z-button:hover .z-button-bm,
.editor-button.z-button:hover .z-button-br {
    background: linear-gradient(180deg, #efb026 0%, #cc7f08 100%);
}

.editor-button img {
    width: 14px;
    height: 14px;
}

.action-button,
.action-text-button {
    font-family: var(--ly-font-sans) !important;
}

.action-button .z-button-cm,
.action-button .z-button-cl,
.action-button .z-button-cr,
.action-button .z-button-tl,
.action-button .z-button-tm,
.action-button .z-button-tr,
.action-button .z-button-bl,
.action-button .z-button-bm,
.action-button .z-button-br,
.action-text-button .z-button-cm,
.action-text-button .z-button-cl,
.action-text-button .z-button-cr,
.action-text-button .z-button-tl,
.action-text-button .z-button-tm,
.action-text-button .z-button-tr,
.action-text-button .z-button-bl,
.action-text-button .z-button-bm,
.action-text-button .z-button-br {
    border-radius: 10px !important;
    font-family: var(--ly-font-sans) !important;
}

.action-text-button .z-button-cm,
.action-button .z-button-cm {
    text-align: center !important;
}

.action-text-button .z-button-cm {
    padding-left: 16px !important;
    padding-right: 16px !important;
    line-height: 32px !important;
}

td.editor-button,
td.editor-button * {
    box-sizing: border-box !important;
}

.action-button,
.action-text-button {
    border-radius: 10px !important;
    overflow: hidden !important;
    min-width: 104px !important;
    font-family: var(--ly-font-sans) !important;
}

.action-button .z-button-cm,
.action-text-button .z-button-cm {
    text-align: center !important;
    font-family: var(--ly-font-sans) !important;
    line-height: 1.2 !important;
}

.action-text-button .z-button-cm {
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    min-height: 36px !important;
}

.app-shell-main-region .z-label,
.app-shell-main-region label,
.app-shell-main-region .z-textbox,
.app-shell-main-region .z-decimalbox,
.app-shell-main-region .z-intbox,
.app-shell-main-region .z-longbox,
.app-shell-main-region .z-doublebox,
.app-shell-main-region .z-datebox-inp,
.app-shell-main-region .z-combobox-inp {
    font-family: var(--ly-font-sans) !important;
}

.editor-button.z-button img,
button.editor-button img,
.editor-button .z-button-cm img {
    display: block !important;
    width: 14px !important;
    height: 14px !important;
    margin: 0 auto !important;
    opacity: 1 !important;
    visibility: visible !important;
}

td.editor-button {
    width: 40px !important;
    min-width: 40px !important;
    max-width: 40px !important;
}

button.editor-button,
.editor-button.z-button {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
}

button.editor-button .z-button-cm,
.editor-button.z-button .z-button-cm {
    text-align: center !important;
    padding: 0 !important;
}

.z-textbox,
.z-decimalbox,
.z-intbox,
.z-longbox,
.z-doublebox,
.z-datebox-inp {
    height: 32px;
    padding: 3px 10px;
    border-radius: 10px;
    border-color: #d8cfbf;
    background: #fffdfa;
    font-size: 13px;
    box-sizing: border-box;
}

.z-textbox:focus,
.z-combobox-inp:focus,
.z-datebox-inp:focus,
input:focus,
textarea:focus {
    border-color: rgba(239, 176, 38, 0.82);
    box-shadow: 0 0 0 4px rgba(239, 176, 38, 0.16);
}

.z-combobox .z-combobox-img,
.z-datebox .z-datebox-img {
    width: 32px;
    min-width: 32px;
    height: 32px;
    background: #f4ede4;
    border-color: #d8cfbf;
    border-radius: 0 10px 10px 0;
}

td.z-group-inner {
    padding: 16px 10px 8px;
}

.z-group-inner .z-group-cnt,
.z-group-inner .z-group-cnt span {
    color: var(--ly-primary);
    letter-spacing: 0.05em;
}

.app-statusbar {
    background: rgba(255, 253, 250, 0.92);
    border-top: 1px solid rgba(22, 32, 39, 0.08);
}

/* Top shell and remaining legacy blue cleanup */
.app-sidepanel {
    background: linear-gradient(180deg, #fffdfa 0%, #f7f1e8 100%);
    border: 1px solid rgba(22, 32, 39, 0.08);
    border-radius: 20px;
    box-shadow: 0 18px 42px rgba(20, 25, 29, 0.08);
}

.app-shell-menu-region .z-west-body,
.app-shell-menu-region .z-west-cnt,
.app-shell-main-region .z-center-body,
.app-shell-main-region .z-center-cnt {
    background: transparent !important;
}

.menu-search,
.adwindow-status,
.adwindow-nav-content,
.z-tabs-scroll,
.z-tabs-scroll .z-tabs-cnt,
.z-tabs,
.z-tabs-content,
.desktop-tabbox,
.desktop-tabpanel,
.z-tabpanel,
.z-tabpanel-cnt,
.z-window-embedded-cnt,
.z-window-modal-cnt,
.z-window-highlighted-cnt,
.z-window-overlapped-cnt {
    background: transparent !important;
}

.desktop-tabbox {
    border: none;
    box-shadow: none;
}

.desktop-tabbox > .z-tabs,
.desktop-tabbox > .z-tabs-scroll,
.desktop-tabbox > .z-tabs > .z-tabs-content,
.desktop-tabbox > .z-tabs > .z-tabs-cnt {
    background: linear-gradient(180deg, rgba(255, 253, 250, 0.98) 0%, rgba(246, 241, 232, 0.98) 100%) !important;
    border-bottom: none !important;
}

.desktop-tabbox > .z-tabs {
    padding: 10px 14px 0;
    border: 1px solid rgba(22, 32, 39, 0.08);
    border-bottom: none;
    border-radius: 18px 18px 0 0;
    box-shadow: 0 14px 34px rgba(20, 25, 29, 0.08);
}

.desktop-tabbox > .z-tabpanels,
.desktop-tabbox > .z-toolbar,
.desktop-tabbox > .window-container-toolbar {
    background: rgba(255, 253, 250, 0.98) !important;
}

.window-container-toolbar,
.adwindow-toolbar,
.toolbar {
    min-height: 64px;
    height: auto !important;
    padding-top: 14px;
    padding-bottom: 14px;
    line-height: normal;
    overflow: visible;
}

.window-container-toolbar {
    border: 1px solid rgba(22, 32, 39, 0.08);
    border-top: none;
    border-bottom: 1px solid rgba(22, 32, 39, 0.08);
    box-shadow: 0 14px 34px rgba(20, 25, 29, 0.08);
}

.toolbar-button,
.toolbar-button-large,
.embedded-toolbar-button,
.window-container-toolbar-btn,
.z-toolbar a,
.z-toolbar a:visited {
    min-height: 36px;
    overflow: visible;
}

.toolbar-button img,
.toolbar-button-large img,
.embedded-toolbar-button img,
.window-container-toolbar-btn img {
    width: 24px;
    height: 24px;
    padding: 8px;
    border-radius: 13px;
}

.window-container-toolbar-btn.tab-list {
    min-height: 40px;
    padding: 0 14px;
}

.z-tab {
    margin-right: 12px;
}

.z-tab .z-tab-hl .z-tab-text,
.z-tab .z-tab-hl:hover .z-tab-text {
    background: #f1ebe2;
    color: #566067;
}

.z-tab-seld .z-tab-hl .z-tab-text,
.z-tab-seld .z-tab-hl:hover .z-tab-text {
    background: #ffffff;
    color: var(--ly-primary);
    box-shadow: 0 12px 26px rgba(20, 25, 29, 0.08), inset 0 0 0 1px rgba(239, 176, 38, 0.28);
}

.app-menu,
.app-sidepanel .app-menu {
    background: transparent;
    box-shadow: none;
}

.app-sidepanel .z-panel-header,
.app-sidepanel .z-panel-header-text {
    color: var(--ly-primary) !important;
}

.app-sidepanel .app-menu-toolbar,
.app-sidepanel .app-menu-footer,
.app-sidepanel .app-menu-expand-toggle,
.app-sidepanel .app-menu .z-label {
    color: var(--ly-text-muted);
    font-family: var(--ly-font-sans) !important;
}

.app-sidepanel .app-menu-searchbar {
    border-bottom: 1px solid rgba(22, 32, 39, 0.08);
}

.app-sidepanel .app-menu-footer {
    border-top: 1px solid rgba(22, 32, 39, 0.08);
}

.app-sidepanel .app-menu-searchbar input,
.app-sidepanel .app-menu-searchbar .z-textbox,
.app-sidepanel .app-menu-searchbar .z-combobox-inp {
    background: #fffdfa;
    border: 1px solid rgba(22, 32, 39, 0.10);
    color: var(--ly-text);
}

.app-sidepanel .app-menu .z-treecell,
.app-sidepanel .app-menu .z-treecell-cnt,
.app-sidepanel .app-menu .z-treecell-text,
.app-sidepanel .app-menu .z-tree-ico,
.app-sidepanel div.z-tree-body td.menu-tree-cell,
.app-sidepanel div.menu-tree-cell-cnt {
    color: var(--ly-text);
    font-family: var(--ly-font-sans) !important;
    font-size: 14px !important;
    line-height: 1.3 !important;
}

.app-sidepanel .app-menu .z-treecell-cnt,
.app-sidepanel div.menu-tree-cell-cnt {
    padding: 8px 10px;
    border-radius: 12px;
    font-weight: 500;
}

.app-sidepanel .app-menu .z-treecell-cnt:hover,
.app-sidepanel div.menu-tree-cell-cnt:hover {
    background: rgba(239, 176, 38, 0.14);
    color: var(--ly-primary);
    transform: none;
}

.app-sidepanel .app-menu .z-treerow-selected .z-treecell-cnt,
.app-sidepanel .app-menu .z-treerow-seld .z-treecell-cnt,
.app-sidepanel .app-menu tr.z-treeitem-seld .z-treecell-cnt,
.app-sidepanel td.menu-tree-cell-seld div.menu-tree-cell-cnt {
    background: linear-gradient(180deg, rgba(242, 181, 47, 0.28), rgba(215, 139, 14, 0.16)) !important;
    color: #11181d !important;
    box-shadow: inset 0 0 0 1px rgba(215, 139, 14, 0.28), inset 3px 0 0 var(--ly-accent) !important;
    font-weight: 700 !important;
}

.app-sidepanel .app-menu span.z-tree-root-open,
.app-sidepanel .app-menu span.z-tree-tee-open,
.app-sidepanel .app-menu span.z-tree-last-open,
.app-sidepanel .app-menu span.z-tree-root-close,
.app-sidepanel .app-menu span.z-tree-tee-close,
.app-sidepanel .app-menu span.z-tree-last-close,
.app-menu span.z-tree-root-open,
.app-menu span.z-tree-tee-open,
.app-menu span.z-tree-last-open,
.app-menu span.z-tree-root-close,
.app-menu span.z-tree-tee-close,
.app-menu span.z-tree-last-close {
    width: 14px !important;
    height: 14px !important;
    margin-right: 4px !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 10px 10px !important;
}

.app-sidepanel .app-menu span.z-tree-root-close,
.app-sidepanel .app-menu span.z-tree-tee-close,
.app-sidepanel .app-menu span.z-tree-last-close,
.app-menu span.z-tree-root-close,
.app-menu span.z-tree-tee-close,
.app-menu span.z-tree-last-close {
    background-image: url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='10' height='10' viewBox='0 0 10 10'%3E%3Cpath fill='none' stroke='%23d78b0e' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.8' d='M3 2l4 3-4 3'/%3E%3C/svg%3E\") !important;
}

.app-sidepanel .app-menu span.z-tree-root-open,
.app-sidepanel .app-menu span.z-tree-tee-open,
.app-sidepanel .app-menu span.z-tree-last-open,
.app-menu span.z-tree-root-open,
.app-menu span.z-tree-tee-open,
.app-menu span.z-tree-last-open {
    background-image: url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='10' height='10' viewBox='0 0 10 10'%3E%3Cpath fill='none' stroke='%23d78b0e' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.8' d='M2 3l3 4 3-4'/%3E%3C/svg%3E\") !important;
}

tr.z-group,
tr.z-group td,
tbody tr.z-group,
tbody tr.z-group td {
    background: transparent !important;
}

td.z-group-inner {
    padding: 18px 12px 10px;
    background: transparent !important;
}

.z-group-inner .z-group-cnt,
.z-group-inner .z-group-cnt span {
    display: inline-block;
    padding: 6px 12px;
    background: rgba(239, 176, 38, 0.12) !important;
    color: var(--ly-primary) !important;
    border-radius: 999px;
    box-shadow: inset 0 0 0 1px rgba(239, 176, 38, 0.18);
}

.z-group-img {
    filter: hue-rotate(12deg) saturate(1.4) brightness(0.95);
}

div.z-grid-header th.z-column,
div.z-grid-header th.z-auxheader,
.z-list-header,
.z-list-header-cnt,
.z-calendar-calyear td,
.z-datebox-calyear td {
    background: #f6f0e7 !important;
    color: var(--ly-text-muted) !important;
    border-bottom: 1px solid rgba(22, 32, 39, 0.08) !important;
}

.z-panel-header,
.app-dashboard-card .z-panel-header,
.z-window-modal-header,
.z-window-popup-header,
.z-window-highlighted-header,
.z-window-overlapped-header,
.z-window-embedded-header {
    background: rgba(255, 253, 250, 0.98) !important;
    color: var(--ly-primary) !important;
    border-bottom: 1px solid rgba(22, 32, 39, 0.08) !important;
}

/* Login specific pass */
.login-shell-stage {
    padding: 64px 20px 40px;
}

.login-shell-panel,
.login-shell-panel-role {
    max-width: 700px;
}

.login-box-header,
.login-box-body,
.login-box-footer {
    width: 680px;
}

.login-box-header {
    padding: 24px 40px 18px;
    background:
        radial-gradient(circle at top left, rgba(239, 176, 38, 0.18) 0, transparent 22%),
        linear-gradient(135deg, #1a242b 0%, #0d1419 100%);
    border-radius: 0;
    box-shadow: 0 28px 48px rgba(13, 20, 25, 0.20);
}

.login-box-header-txt {
    margin-top: 0;
    color: #f7e6b2;
    font-family: var(--ly-font-sans);
    font-size: 24px;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    text-shadow: none;
}

.login-box-body {
    padding: 18px 40px 10px;
    background: rgba(255, 253, 250, 0.98);
    border-left: 1px solid rgba(22, 32, 39, 0.08);
    border-right: 1px solid rgba(22, 32, 39, 0.08);
    box-shadow: 0 22px 40px rgba(13, 20, 25, 0.10);
}

.login-box-header-logo {
    padding: 4px 0 18px;
    text-align: center;
}

.login-box-header-logo img {
    max-width: 320px;
    max-height: 92px;
    width: auto;
    height: auto;
}

.login-label {
    color: var(--ly-text-muted);
    font-family: var(--ly-font-sans);
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

.login-field,
.login-field *,
.login-box-body input,
.login-box-body select,
.login-box-body .z-textbox,
.login-box-body .z-listbox,
.login-box-body .z-combobox-inp,
.login-box-body .z-checkbox-cnt,
.login-box-body label,
.login-box-body span {
    font-family: var(--ly-font-sans);
}

.login-box-body .z-textbox,
.login-box-body .z-decimalbox,
.login-box-body .z-intbox,
.login-box-body .z-longbox,
.login-box-body .z-doublebox,
.login-box-body .z-datebox-inp {
    height: 32px;
    padding: 3px 10px;
    border-radius: 10px;
    border: 1px solid #d8cfbf;
    background: #fffdfa;
    color: var(--ly-text);
    font-size: 14px;
    font-weight: 600;
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.9);
    box-sizing: border-box;
}

.login-box-body .z-textbox:focus,
.login-box-body .z-combobox-inp:focus,
.login-box-body .z-datebox-inp:focus,
.login-box-body input:focus,
.login-box-body textarea:focus {
    border-color: rgba(239, 176, 38, 0.86);
    box-shadow: 0 0 0 4px rgba(239, 176, 38, 0.16);
}

.login-box-body .z-checkbox-cnt,
.login-box-body label,
.login-box-body span {
    color: var(--ly-text);
    font-size: 14px;
}

.login-box-footer {
    padding: 6px 40px 34px;
    background: linear-gradient(180deg, #eef1f3 0%, #e5eaee 100%);
    border: 1px solid rgba(22, 32, 39, 0.08);
    border-top: none;
    border-radius: 0;
    box-shadow: 0 26px 42px rgba(13, 20, 25, 0.14);
    text-align: center;
}

.login-box-footer-pnl {
    display: flex !important;
    justify-content: center;
    align-items: center;
    gap: 16px;
    width: 100% !important;
    margin: 10px auto 0 !important;
    padding-top: 0;
}

.login-box-footer-pnl .login-btn {
    display: inline-block !important;
    margin: 0 !important;
}

.login-box-footer .z-button,
.login-box-footer .login-btn {
    width: 168px !important;
    min-width: 168px !important;
    max-width: 168px !important;
    height: 42px;
    border-radius: 11px;
    margin-right: 0;
    background-image: none;
    font-family: var(--ly-font-sans);
    font-size: 14px;
    font-weight: 700;
    letter-spacing: 0.01em;
    white-space: nowrap;
}

.login-box-footer .z-button-cm,
.login-box-footer .z-button-cl,
.login-box-footer .z-button-cr,
.login-box-footer .z-button-tl,
.login-box-footer .z-button-tm,
.login-box-footer .z-button-tr,
.login-box-footer .z-button-bl,
.login-box-footer .z-button-bm,
.login-box-footer .z-button-br {
    text-align: center;
}

.login-box-footer .z-button img {
    vertical-align: middle;
}

.login-box-footer #Ok .z-button-cm,
.login-box-footer #Ok .z-button-cl,
.login-box-footer #Ok .z-button-cr,
.login-box-footer #Ok .z-button-tl,
.login-box-footer #Ok .z-button-tm,
.login-box-footer #Ok .z-button-tr,
.login-box-footer #Ok .z-button-bl,
.login-box-footer #Ok .z-button-bm,
.login-box-footer #Ok .z-button-br {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%);
    background-image: none;
    color: #13191d;
    box-shadow: inset 0 1px 0 rgba(255, 245, 214, 0.75);
}

.login-box-footer #Ok:hover .z-button-cm,
.login-box-footer #Ok:hover .z-button-cl,
.login-box-footer #Ok:hover .z-button-cr,
.login-box-footer #Ok:hover .z-button-tl,
.login-box-footer #Ok:hover .z-button-tm,
.login-box-footer #Ok:hover .z-button-tr,
.login-box-footer #Ok:hover .z-button-bl,
.login-box-footer #Ok:hover .z-button-bm,
.login-box-footer #Ok:hover .z-button-br {
    background: linear-gradient(180deg, #f7c85a 0%, #e19917 100%);
}

.login-box-footer #Customize .z-button-cm,
.login-box-footer #Customize .z-button-cl,
.login-box-footer #Customize .z-button-cr,
.login-box-footer #Customize .z-button-tl,
.login-box-footer #Customize .z-button-tm,
.login-box-footer #Customize .z-button-tr,
.login-box-footer #Customize .z-button-bl,
.login-box-footer #Customize .z-button-bm,
.login-box-footer #Customize .z-button-br {
    background: linear-gradient(180deg, #1b252c 0%, #11181d 100%);
    background-image: none;
    color: #f9fbfb;
}

.login-box-footer #Customize:hover .z-button-cm,
.login-box-footer #Customize:hover .z-button-cl,
.login-box-footer #Customize:hover .z-button-cr,
.login-box-footer #Customize:hover .z-button-tl,
.login-box-footer #Customize:hover .z-button-tm,
.login-box-footer #Customize:hover .z-button-tr,
.login-box-footer #Customize:hover .z-button-bl,
.login-box-footer #Customize:hover .z-button-bm,
.login-box-footer #Customize:hover .z-button-br {
    background: linear-gradient(180deg, #26323a 0%, #161f25 100%);
}

.login-box-footer #Ok,
.login-box-footer #Customize {
    width: 168px !important;
    min-width: 168px !important;
    max-width: 168px !important;
}

.login-box-footer #Customize img {
    display: none;
}

.login-box-footer #Customize .z-button-cm,
.login-box-footer #Customize .z-button-cl,
.login-box-footer #Customize .z-button-cr,
.login-box-footer #Customize .z-button-tl,
.login-box-footer #Customize .z-button-tm,
.login-box-footer #Customize .z-button-tr,
.login-box-footer #Customize .z-button-bl,
.login-box-footer #Customize .z-button-bm,
.login-box-footer #Customize .z-button-br {
    text-align: center;
}

.login-box-footer #Customize .z-button-cm {
    white-space: nowrap;
}

.login-shell-panel .login-box-footer-pnl {
    display: block !important;
    width: 100% !important;
    margin: 12px auto 0 !important;
    max-width: none !important;
    padding: 0 !important;
    text-align: center !important;
    font-size: 0;
}

.login-shell-panel .login-box-footer-pnl .z-button.login-btn {
    display: inline-table !important;
    table-layout: fixed;
    border-collapse: separate;
    border-spacing: 0;
    float: none !important;
    vertical-align: top !important;
    width: 184px !important;
    min-width: 184px !important;
    max-width: 184px !important;
    height: 40px !important;
    margin: 0 10px !important;
    border-radius: 12px !important;
    overflow: hidden;
}

.login-shell-panel .login-box-footer-pnl .z-button.login-btn .z-button-cm,
.login-shell-panel .login-box-footer-pnl .z-button.login-btn .z-button-cl,
.login-shell-panel .login-box-footer-pnl .z-button.login-btn .z-button-cr,
.login-shell-panel .login-box-footer-pnl .z-button.login-btn .z-button-tl,
.login-shell-panel .login-box-footer-pnl .z-button.login-btn .z-button-tm,
.login-shell-panel .login-box-footer-pnl .z-button.login-btn .z-button-tr,
.login-shell-panel .login-box-footer-pnl .z-button.login-btn .z-button-bl,
.login-shell-panel .login-box-footer-pnl .z-button.login-btn .z-button-bm,
.login-shell-panel .login-box-footer-pnl .z-button.login-btn .z-button-br {
    height: 40px !important;
    padding: 0 14px !important;
    text-align: center !important;
    vertical-align: middle !important;
    white-space: nowrap !important;
}

.login-shell-panel .login-box-footer-pnl .z-button.login-btn .z-button-cm {
    font-size: 14px;
    font-weight: 700;
    letter-spacing: 0.01em;
    overflow: visible;
    text-align: center !important;
}

.login-shell-panel .login-box-footer-pnl #Customize img {
    display: none !important;
}

.login-shell-panel .login-box-footer-pnl #Ok img {
    display: inline-block !important;
    margin-right: 8px;
    vertical-align: middle;
}

.login-shell-panel .login-box-footer-pnl #Customize .z-button-cm {
    color: #f8fbfc;
}

.login-shell-panel .login-box-footer-pnl #Ok .z-button-cm {
    color: #13191d;
}

@media screen and (max-width: 760px) {
    .login-shell-panel {
        width: min(100%, 540px) !important;
    }

    .login-shell-panel .login-box-body {
        width: auto;
        padding-left: 18px;
        padding-right: 18px;
    }

    .login-shell-panel .login-box-footer {
        padding-left: 20px;
        padding-right: 20px;
    }

    .login-shell-panel .login-form-grid,
    .login-shell-panel .login-form-grid tbody,
    .login-shell-panel .login-form-grid tr,
    .login-shell-panel .login-form-grid td {
        display: block;
        width: 100% !important;
        text-align: left !important;
    }

    .login-shell-panel .login-label {
        padding-bottom: 6px;
    }

    .login-shell-panel .login-box-footer-pnl .z-button.login-btn {
        display: block !important;
        width: min(100%, 260px) !important;
        min-width: 0 !important;
        max-width: 260px !important;
        margin: 8px auto !important;
    }
}

/* Hard override for legacy ZK login footer buttons */
.login-shell-panel .login-box-footer {
    text-align: center !important;
}

.login-shell-panel .login-box-footer-pnl {
    display: inline-block !important;
    width: auto !important;
    max-width: none !important;
    margin: 10px auto 0 !important;
    padding: 0 !important;
    text-align: center !important;
    white-space: nowrap !important;
    font-size: 0;
}

.login-shell-panel .login-box-footer-pnl #Customize,
.login-shell-panel .login-box-footer-pnl #Ok {
    display: inline-table !important;
    float: none !important;
    clear: none !important;
    position: static !important;
    vertical-align: middle !important;
    width: 170px !important;
    min-width: 170px !important;
    max-width: 170px !important;
    height: 36px !important;
    margin: 0 12px !important;
    border-radius: 12px !important;
    overflow: hidden !important;
    border-collapse: separate !important;
    border-spacing: 0 !important;
}

.login-shell-panel .login-box-footer-pnl #Customize .z-button-tl,
.login-shell-panel .login-box-footer-pnl #Customize .z-button-tm,
.login-shell-panel .login-box-footer-pnl #Customize .z-button-tr,
.login-shell-panel .login-box-footer-pnl #Customize .z-button-cl,
.login-shell-panel .login-box-footer-pnl #Customize .z-button-cm,
.login-shell-panel .login-box-footer-pnl #Customize .z-button-cr,
.login-shell-panel .login-box-footer-pnl #Customize .z-button-bl,
.login-shell-panel .login-box-footer-pnl #Customize .z-button-bm,
.login-shell-panel .login-box-footer-pnl #Customize .z-button-br,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-tl,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-tm,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-tr,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-cl,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-cm,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-cr,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-bl,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-bm,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-br {
    height: 36px !important;
    padding: 0 12px !important;
    text-align: center !important;
    vertical-align: middle !important;
    white-space: nowrap !important;
}

.login-shell-panel .login-box-footer-pnl #Customize .z-button-cm,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-cm {
    font-size: 13px !important;
    font-weight: 700 !important;
    line-height: 36px !important;
    text-align: center !important;
}

.login-shell-panel .login-box-footer-pnl #Customize img {
    display: none !important;
}

.login-shell-panel .login-box-footer-pnl #Ok img {
    display: inline-block !important;
    margin-right: 6px !important;
    vertical-align: middle !important;
}

@media screen and (max-width: 760px) {
    .login-shell-panel .login-box-footer-pnl {
        display: block !important;
        white-space: normal !important;
    }

    .login-shell-panel .login-box-footer-pnl #Customize,
    .login-shell-panel .login-box-footer-pnl #Ok {
        display: block !important;
        width: min(100%, 240px) !important;
        min-width: 0 !important;
        max-width: 240px !important;
        margin: 8px auto !important;
    }
}

/* Ultimate override for ZK 3.x login buttons */
.login-shell-panel .login-box-footer {
    text-align: center !important;
}

.login-shell-panel .login-box-footer-pnl {
    display: block !important;
    width: 392px !important;
    max-width: 392px !important;
    margin: 8px auto 0 !important;
    padding: 0 !important;
    text-align: center !important;
    white-space: nowrap !important;
    overflow: visible !important;
}

.login-shell-panel .login-box-footer-pnl #Customize,
.login-shell-panel .login-box-footer-pnl #Ok {
    display: inline-table !important;
    float: none !important;
    clear: none !important;
    width: 170px !important;
    min-width: 170px !important;
    max-width: 170px !important;
    height: auto !important;
    margin: 0 10px !important;
    padding: 0 !important;
    vertical-align: top !important;
    background: transparent !important;
    box-shadow: 0 0 0 10px rgba(255, 253, 250, 0.98) !important;
}

.login-shell-panel .login-box-footer-pnl #Customize .z-button-tl,
.login-shell-panel .login-box-footer-pnl #Customize .z-button-tm,
.login-shell-panel .login-box-footer-pnl #Customize .z-button-tr,
.login-shell-panel .login-box-footer-pnl #Customize .z-button-bl,
.login-shell-panel .login-box-footer-pnl #Customize .z-button-bm,
.login-shell-panel .login-box-footer-pnl #Customize .z-button-br,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-tl,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-tm,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-tr,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-bl,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-bm,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-br {
    height: 0 !important;
    line-height: 0 !important;
    font-size: 0 !important;
    padding: 0 !important;
    background: transparent !important;
}

.login-shell-panel .login-box-footer-pnl #Customize .z-button-cl,
.login-shell-panel .login-box-footer-pnl #Customize .z-button-cr,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-cl,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-cr {
    width: 0 !important;
    padding: 0 !important;
    background: transparent !important;
}

.login-shell-panel .login-box-footer-pnl #Customize .z-button-cm,
.login-shell-panel .login-box-footer-pnl #Ok .z-button-cm {
    display: block !important;
    height: 32px !important;
    line-height: 32px !important;
    padding: 0 12px !important;
    border-radius: 10px !important;
    text-align: center !important;
    white-space: nowrap !important;
    overflow: hidden !important;
    font-size: 13px !important;
    font-weight: 700 !important;
}

.login-shell-panel .login-box-footer-pnl #Customize .z-button-cm {
    background: linear-gradient(180deg, #1b252c 0%, #11181d 100%) !important;
    color: #f8fbfc !important;
}

.login-shell-panel .login-box-footer-pnl #Ok .z-button-cm {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    color: #13191d !important;
}

.login-shell-panel .login-box-footer-pnl #Customize img {
    display: none !important;
}

.login-shell-panel .login-box-footer-pnl #Ok img {
    display: inline-block !important;
    height: 16px !important;
    width: 16px !important;
    margin-right: 6px !important;
    vertical-align: text-bottom !important;
}

@media screen and (max-width: 760px) {
    .login-shell-panel .login-box-footer-pnl {
        width: auto !important;
        max-width: none !important;
        white-space: normal !important;
    }

    .login-shell-panel .login-box-footer-pnl #Customize,
    .login-shell-panel .login-box-footer-pnl #Ok {
        display: block !important;
        width: 220px !important;
        min-width: 220px !important;
        max-width: 220px !important;
        margin: 8px auto !important;
        box-shadow: none !important;
    }
}

.login-feedback-text {
    color: #c25443;
    font-family: var(--ly-font-sans);
    font-size: 12px;
    font-weight: 700;
}

.versionInfoBox {
    margin-top: 14px;
    background: #f7f1e7;
    border: 1px solid rgba(22, 32, 39, 0.08);
    color: var(--ly-text-muted);
}

/* Runtime DOM-specific login footer override */
.login-shell-panel .login-box-footer {
    text-align: center !important;
}

.login-shell-panel .login-box-footer-pnl {
    display: block !important;
    width: 100% !important;
    max-width: none !important;
    margin: 8px auto 0 !important;
    padding: 0 !important;
    text-align: center !important;
    white-space: nowrap !important;
    font-size: 0 !important;
}

.login-shell-panel .login-box-footer-pnl > span.z-button {
    display: inline-block !important;
    float: none !important;
    clear: none !important;
    vertical-align: top !important;
    margin: 0 12px !important;
    padding: 0 !important;
}

.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-secondary,
.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-primary {
    width: 168px !important;
    min-width: 168px !important;
    max-width: 168px !important;
    height: 34px !important;
    border-collapse: separate !important;
    border-spacing: 0 !important;
}

.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-secondary tr:first-child,
.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-secondary tr:last-child,
.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-primary tr:first-child,
.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-primary tr:last-child {
    display: none !important;
}

.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-secondary .z-button-cl,
.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-secondary .z-button-cr,
.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-primary .z-button-cl,
.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-primary .z-button-cr {
    width: 0 !important;
    padding: 0 !important;
    background: transparent !important;
}

.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-secondary .z-button-cm,
.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-primary .z-button-cm {
    height: 34px !important;
    line-height: 34px !important;
    padding: 0 12px !important;
    border-radius: 10px !important;
    text-align: center !important;
    white-space: nowrap !important;
    font-size: 13px !important;
    font-weight: 700 !important;
    overflow: hidden !important;
}

.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-secondary .z-button-cm {
    background: linear-gradient(180deg, #1b252c 0%, #11181d 100%) !important;
    color: #f8fbfc !important;
}

.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-primary .z-button-cm {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    color: #13191d !important;
}

.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-secondary img {
    display: none !important;
}

.login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-primary img {
    display: inline-block !important;
    width: 14px !important;
    height: 14px !important;
    margin-right: 6px !important;
    vertical-align: text-bottom !important;
}

@media screen and (max-width: 760px) {
    .login-shell-panel .login-box-footer-pnl {
        white-space: normal !important;
    }

    .login-shell-panel .login-box-footer-pnl > span.z-button {
        display: block !important;
        margin: 8px auto !important;
    }

    .login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-secondary,
    .login-shell-panel .login-box-footer-pnl > span.z-button > table.login-btn.login-btn-primary {
        width: 220px !important;
        min-width: 220px !important;
        max-width: 220px !important;
    }
}

/* Role selection step */
.login-shell-panel-role .login-box-footer {
    text-align: center !important;
}

.login-shell-panel-role .login-box-footer-pnl {
    display: table !important;
    width: auto !important;
    margin: 8px auto 0 !important;
    padding: 0 !important;
    text-align: center !important;
}

.login-shell-panel-role .login-box-footer-pnl .z-hbox {
    display: inline-table !important;
    width: auto !important;
    margin: 0 10px !important;
}

.login-shell-panel-role .login-box-footer-pnl .z-hbox td {
    padding: 0 !important;
    vertical-align: middle !important;
}

.login-shell-panel-role .login-box-footer-pnl span.z-button {
    display: inline-block !important;
    margin: 0 !important;
    float: none !important;
}

.login-shell-panel-role .login-box-footer-pnl table.login-btn {
    width: 84px !important;
    min-width: 84px !important;
    max-width: 84px !important;
    height: 34px !important;
    border-collapse: separate !important;
    border-spacing: 0 !important;
}

.login-shell-panel-role .login-box-footer-pnl table.login-btn tr:first-child,
.login-shell-panel-role .login-box-footer-pnl table.login-btn tr:last-child {
    display: none !important;
}

.login-shell-panel-role .login-box-footer-pnl table.login-btn .z-button-cl,
.login-shell-panel-role .login-box-footer-pnl table.login-btn .z-button-cr {
    width: 0 !important;
    padding: 0 !important;
    background: transparent !important;
}

.login-shell-panel-role .login-box-footer-pnl table.login-btn .z-button-cm {
    height: 34px !important;
    line-height: 34px !important;
    padding: 0 12px !important;
    border-radius: 10px !important;
    text-align: center !important;
    background: linear-gradient(180deg, #1b252c 0%, #11181d 100%) !important;
    color: #f8fbfc !important;
    font-size: 13px !important;
    font-weight: 700 !important;
    white-space: nowrap !important;
}

.login-shell-panel-role .login-box-footer-pnl table.login-btn img {
    width: 14px !important;
    height: 14px !important;
    vertical-align: text-bottom !important;
}

.login-shell-panel-role .z-combobox table,
.login-shell-panel-role .z-combobox table td {
    border: none !important;
}

.login-shell-panel-role .z-combobox-inp {
    font-family: var(--ly-font-sans) !important;
    font-size: 14px !important;
    font-weight: 600 !important;
    color: var(--ly-text) !important;
}

.login-shell-panel-role .z-combobox-btn {
    background: #f6efe2 !important;
    border-left: 1px solid #d8cfbf !important;
}

.login-shell-panel-role .z-combobox-img {
    background-color: #f6efe2 !important;
}

.login-shell-panel-role .z-combobox-pp {
    background: #fffdfa !important;
    border: 1px solid #d7c8af !important;
    box-shadow: 0 16px 28px rgba(13, 20, 25, 0.14) !important;
    padding: 4px !important;
    font-family: var(--ly-font-sans) !important;
}

.login-shell-panel-role .z-combobox-pp .z-combo-item,
.login-shell-panel-role .z-combobox-pp .z-combo-item-text {
    font-family: var(--ly-font-sans) !important;
    font-size: 14px !important;
    color: var(--ly-text) !important;
}

.login-shell-panel-role .z-combobox-pp .z-combo-item td {
    border: none !important;
    padding-top: 4px !important;
    padding-bottom: 4px !important;
}

.login-shell-panel-role .z-combobox-pp .z-combo-item-over {
    background: #f7efe2 !important;
}

.login-shell-panel-role .z-combobox-pp .z-combo-item-seld,
.login-shell-panel-role .z-combobox-pp .z-combo-item-over-seld {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    border: 1px solid #d18a14 !important;
}

.login-shell-panel-role .z-combobox-pp .z-combo-item-seld .z-combo-item-text,
.login-shell-panel-role .z-combobox-pp .z-combo-item-over-seld .z-combo-item-text {
    color: #13191d !important;
    font-weight: 700 !important;
}

@media screen and (max-width: 760px) {
    .login-shell-panel-role .login-box-footer-pnl .z-hbox {
        display: block !important;
        margin: 8px auto !important;
    }
}

.login-shell-panel-role .login-box-footer-pnl .z-hbox:first-child table.login-btn .z-button-cm,
.login-shell-panel-role .login-box-footer-pnl span.z-button:first-child table.login-btn .z-button-cm {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    color: #13191d !important;
}

.login-shell-panel-role .login-box-footer-pnl .z-hbox:last-child table.login-btn .z-button-cm,
.login-shell-panel-role .login-box-footer-pnl span.z-button:last-child table.login-btn .z-button-cm {
    background: linear-gradient(180deg, #1b252c 0%, #11181d 100%) !important;
    color: #f8fbfc !important;
}

/* Global combobox refresh */
.z-combobox table td:last-child {
    width: 32px !important;
    height: 32px !important;
    padding: 0 !important;
    vertical-align: top !important;
    box-sizing: border-box !important;
}

.login-box-body .z-combobox,
.login-shell-panel-role .z-combobox {
    height: 32px !important;
    margin-bottom: 6px !important;
}

.login-box-body .z-combobox table,
.login-shell-panel-role .z-combobox table {
    height: 32px !important;
    border-collapse: collapse !important;
}

.login-box-body .z-combobox-inp,
.login-shell-panel-role .z-combobox-inp {
    height: 32px !important;
    line-height: 22px !important;
    padding-top: 3px !important;
    padding-bottom: 3px !important;
    box-sizing: border-box !important;
    border-radius: 10px 0 0 10px !important;
    border-right: none !important;
}

.z-combobox .z-combobox-btn {
    display: inline-flex !important;
    align-items: center !important;
    justify-content: center !important;
    width: 32px !important;
    height: 32px !important;
    margin-left: 0 !important;
    background: #f6efe2 !important;
    border-left: 1px solid #d8cfbf !important;
    box-sizing: border-box !important;
}

.login-box-body .z-combobox .z-combobox-btn,
.login-shell-panel-role .z-combobox .z-combobox-btn {
    width: 32px !important;
    height: 32px !important;
}

.z-combobox .z-combobox-img {
    width: 32px !important;
    min-width: 32px !important;
    height: 32px !important;
    display: block !important;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8' viewBox='0 0 12 8'%3E%3Cpath fill='%23232d34' d='M1.41.59 6 5.17 10.59.59 12 2l-6 6-6-6z'/%3E%3C/svg%3E") !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 13px 9px !important;
    background-color: #f6efe2 !important;
    border: 1px solid #d8cfbf !important;
    border-left: none !important;
    box-sizing: border-box !important;
}

.login-box-body .z-combobox .z-combobox-img,
.login-shell-panel-role .z-combobox .z-combobox-img {
    width: 32px !important;
    min-width: 32px !important;
    height: 32px !important;
}

.z-combobox-focus .z-combobox-btn,
.z-combobox-focus .z-combobox-img {
    border-color: rgba(239, 176, 38, 0.86) !important;
    background-color: #fbf1da !important;
}

/* App shell refresh - pass 1 */
.app-header {
    padding: 0 18px !important;
}

.app-header-logo {
    max-height: 40px !important;
    width: auto !important;
}

.app-shell-menu-region {
    min-width: 330px !important;
}

.app-menu,
.app-sidepanel .app-menu {
    font-family: var(--ly-font-sans) !important;
}

.app-menu .z-panel-header {
    padding: 16px 18px 6px !important;
    color: #f2d48a !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 17px !important;
    font-weight: 700 !important;
    letter-spacing: 0.05em !important;
}

.app-menu-toolbar {
    padding: 12px 18px 10px !important;
}

.app-menu-searchbar {
    border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

.app-menu-searchbar .z-combobox,
.app-menu-searchbar .z-combobox table,
.app-menu-searchbar .z-textbox {
    width: 256px !important;
    max-width: 256px !important;
}

.app-menu-searchbar .z-combobox {
    display: inline-block !important;
    vertical-align: middle !important;
    min-width: 256px !important;
}

.app-menu-searchbar .z-combobox table {
    table-layout: fixed !important;
}

.app-menu-searchbar .z-combobox table td:first-child {
    width: 224px !important;
}

.app-menu-searchbar .z-combobox-inp,
.app-menu-searchbar .z-textbox,
.app-menu-searchbar input {
    height: 36px !important;
    padding: 6px 12px !important;
    border-radius: 12px 0 0 12px !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 14px !important;
    font-weight: 600 !important;
    box-sizing: border-box !important;
}

.app-menu-searchbar .z-combobox-inp {
    border-right: none !important;
    width: 224px !important;
}

.app-menu-searchbar .z-combobox .z-combobox-btn,
.app-menu-searchbar .z-combobox .z-combobox-img {
    width: 30px !important;
    min-width: 30px !important;
    height: 36px !important;
}

.app-menu .z-treecell-cnt,
div.menu-tree-cell-cnt,
.app-menu .z-treecell-text,
.app-menu .z-treecell,
.app-menu .z-tree-ico {
    font-family: var(--ly-font-sans) !important;
    font-size: 14px !important;
    line-height: 1.3 !important;
}

.app-menu .z-treecell-cnt,
div.menu-tree-cell-cnt {
    padding: 9px 12px !important;
    border-radius: 14px !important;
}

.desktop-tabbox > .z-tabs,
.desktop-tabbox > .z-tabs-scroll,
.desktop-tabbox > .z-tabs > .z-tabs-content,
.desktop-tabbox > .z-tabs > .z-tabs-cnt,
.z-tabs-scroll,
.z-tabs-scroll .z-tabs-cnt {
    background: linear-gradient(180deg, #1c242b 0%, #0f1519 100%) !important;
    border: none !important;
    box-shadow: none !important;
}

.desktop-tabbox > .z-tabs {
    padding: 12px 16px 0 !important;
    border-radius: 18px 18px 0 0 !important;
}

.z-tab {
    margin-right: 10px !important;
}

.z-tab .z-tab-hl .z-tab-text,
.z-tab .z-tab-hl:hover .z-tab-text {
    padding: 10px 16px !important;
    border-radius: 999px !important;
    background: rgba(255, 255, 255, 0.06) !important;
    color: rgba(255, 255, 255, 0.72) !important;
    box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.08) !important;
}

.z-tab-seld .z-tab-hl .z-tab-text,
.z-tab-seld .z-tab-hl:hover .z-tab-text {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    color: #11181d !important;
    box-shadow: inset 0 0 0 1px rgba(255, 221, 143, 0.36), 0 10px 22px rgba(11, 15, 18, 0.26) !important;
}

.window-container-toolbar,
.adwindow-toolbar,
.toolbar {
    min-height: 74px !important;
    height: auto !important;
    padding: 16px 18px 14px !important;
    line-height: normal !important;
    overflow: visible !important;
}

.window-container-toolbar {
    background: rgba(255, 253, 250, 0.98) !important;
}

.toolbar-button,
.toolbar-button-large,
.embedded-toolbar-button,
.window-container-toolbar-btn,
.z-toolbar a,
.z-toolbar a:visited {
    display: inline-block !important;
    margin-right: 10px !important;
    margin-bottom: 6px !important;
    vertical-align: middle !important;
}

.toolbar-button img,
.toolbar-button-large img,
.embedded-toolbar-button img,
.window-container-toolbar-btn img {
    width: 22px !important;
    height: 22px !important;
    padding: 8px !important;
    border-radius: 12px !important;
}

.z-combobox-pp {
    background: #fffdfa !important;
    border: 1px solid #d7c8af !important;
    box-shadow: 0 16px 28px rgba(13, 20, 25, 0.14) !important;
    padding: 4px !important;
    font-family: var(--ly-font-sans) !important;
}

.z-combobox-pp .z-combo-item,
.z-combobox-pp .z-combo-item-text {
    font-family: var(--ly-font-sans) !important;
    font-size: 14px !important;
    color: var(--ly-text) !important;
}

.z-combobox-pp .z-combo-item td {
    border: none !important;
    padding-top: 4px !important;
    padding-bottom: 4px !important;
}

.z-combobox-pp .z-combo-item-over {
    background: #f7efe2 !important;
}

.z-combobox-pp .z-combo-item-seld,
.z-combobox-pp .z-combo-item-over-seld {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    border: 1px solid #d18a14 !important;
}

.z-combobox-pp .z-combo-item-seld .z-combo-item-text,
.z-combobox-pp .z-combo-item-over-seld .z-combo-item-text {
    color: #13191d !important;
    font-weight: 700 !important;
}

/* App shell refresh - pass 2 */
.desktop-tabbox,
.desktop-tabpanel,
.z-tabpanel,
.z-tabpanel-cnt,
.app-shell-main-region,
.app-shell-main-region .z-center-body,
.app-shell-main-region .z-center-cnt {
    background: #f3eee5 !important;
}

.desktop-tabbox > .z-tabs,
.desktop-tabbox > .z-tabs-scroll,
.desktop-tabbox > .z-tabs > .z-tabs-content,
.desktop-tabbox > .z-tabs > .z-tabs-cnt,
.z-tabs-scroll,
.z-tabs-scroll .z-tabs-cnt {
    background: linear-gradient(180deg, #1b2329 0%, #10161a 100%) !important;
}

.z-tab .z-tab-hl .z-tab-text,
.z-tab .z-tab-hl:hover .z-tab-text {
    background: rgba(255, 255, 255, 0.05) !important;
    color: rgba(255, 255, 255, 0.72) !important;
    box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.08) !important;
}

.z-tab-seld .z-tab-hl,
.z-tab-seld .z-tab-hm,
.z-tab-seld .z-tab-hr {
    background: transparent !important;
}

.z-tab-seld .z-tab-hl .z-tab-text,
.z-tab-seld .z-tab-hl:hover .z-tab-text {
    background: linear-gradient(180deg, #1b2329 0%, #10161a 100%) !important;
    color: #f2b52f !important;
    box-shadow: inset 0 0 0 1px rgba(242, 181, 47, 0.55), 0 10px 22px rgba(11, 15, 18, 0.22) !important;
}

.z-tab-close {
    background-image: none !important;
    width: 16px !important;
    height: 16px !important;
    margin-left: 6px !important;
    position: relative !important;
    opacity: 1 !important;
}

.z-tab-close:before {
    content: "x";
    color: #f8fbfc;
    font-size: 13px;
    line-height: 16px;
    font-weight: 700;
    position: absolute;
    inset: 0;
    text-align: center;
}

.window-container-toolbar,
.adwindow-toolbar,
.toolbar {
    min-height: 98px !important;
    height: auto !important;
    padding: 20px 20px 18px !important;
    background: linear-gradient(180deg, rgba(255, 253, 250, 0.98) 0%, rgba(244, 238, 229, 0.98) 100%) !important;
    border-color: rgba(22, 32, 39, 0.10) !important;
    overflow: visible !important;
}

.app-shell-main-region .z-toolbar:not(.z-toolbar-tabs):not(.app-menu-toolbar):not(.window-container-toolbar),
.app-shell-main-region .adwindow-toolbar {
    min-height: 72px !important;
    height: auto !important;
    padding-top: 18px !important;
    padding-bottom: 16px !important;
    overflow: visible !important;
}

.app-shell-main-region .z-panel-body,
.app-shell-main-region .z-panel-children,
.app-shell-main-region .z-window-embedded,
.app-shell-main-region .z-window-embedded-cnt,
.app-shell-main-region .z-window-embedded-cnt-noborder {
    overflow: visible !important;
}

.toolbar-button,
.toolbar-button-large,
.embedded-toolbar-button,
.window-container-toolbar-btn,
.z-toolbar a,
.z-toolbar a:visited {
    margin-right: 12px !important;
    margin-bottom: 8px !important;
}

.toolbar-button img,
.toolbar-button-large img,
.embedded-toolbar-button img,
.window-container-toolbar-btn img {
    background: #f5efe7 !important;
    box-shadow: inset 0 0 0 1px rgba(22, 32, 39, 0.08) !important;
}

.toolbar-button img:hover,
.toolbar-button-large img:hover,
.embedded-toolbar-button img:hover,
.window-container-toolbar-btn img:hover {
    background: rgba(242, 181, 47, 0.18) !important;
    box-shadow: inset 0 0 0 1px rgba(242, 181, 47, 0.45) !important;
}

.adwindow-left-nav,
.adwindow-right-nav {
    border-radius: 0 !important;
    background: linear-gradient(180deg, #1b2329 0%, #10161a 100%) !important;
}

/* Menu tree final overrides */
.app-sidepanel .app-menu,
.app-sidepanel .app-menu *,
.app-menu-tree,
.app-menu-tree * {
    font-family: var(--ly-font-sans) !important;
}

.app-sidepanel .app-menu td.menu-tree-cell-seld,
.app-sidepanel .app-menu td.menu-tree-cell-seld div,
.app-sidepanel .app-menu tr.z-treeitem-seld td,
.app-sidepanel .app-menu tr.z-treerow-seld td,
.app-sidepanel .app-menu .z-treerow-selected td,
.app-sidepanel .app-menu .z-treerow-seld td {
    background: transparent !important;
}

.app-sidepanel .app-menu td.menu-tree-cell-seld div.menu-tree-cell-cnt,
.app-sidepanel .app-menu tr.z-treeitem-seld .z-treecell-cnt,
.app-sidepanel .app-menu tr.z-treerow-seld .z-treecell-cnt,
.app-sidepanel .app-menu .z-treerow-selected .z-treecell-cnt,
.app-sidepanel .app-menu .z-treerow-seld .z-treecell-cnt {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    color: #11181d !important;
    box-shadow: inset 0 0 0 1px rgba(209, 138, 20, 0.45) !important;
    font-weight: 700 !important;
}

/* Tab close alignment */
.z-tab {
    position: relative !important;
}

.z-tab .z-tab-hl .z-tab-text,
.z-tab .z-tab-hl:hover .z-tab-text,
.z-tab-seld .z-tab-hl .z-tab-text,
.z-tab-seld .z-tab-hl:hover .z-tab-text {
    position: relative !important;
    padding-right: 28px !important;
}

.z-tab-close {
    position: absolute !important;
    top: 50% !important;
    right: 10px !important;
    left: auto !important;
    margin: -8px 0 0 0 !important;
    z-index: 2 !important;
}

/* Menu selection final final override */
.app-menu,
.app-menu *,
.app-menu-tree,
.app-menu-tree * {
    font-family: var(--ly-font-sans) !important;
}

.app-menu td.menu-tree-cell-seld,
.app-menu td.menu-tree-cell-seld *,
.app-menu tr.z-treeitem-seld td,
.app-menu tr.z-treerow-seld td,
.app-menu .z-treerow-selected td,
.app-menu .z-treerow-seld td {
    background: transparent !important;
    background-color: transparent !important;
}

.app-menu td.menu-tree-cell-seld div.menu-tree-cell-cnt,
.app-menu td.menu-tree-cell-seld .z-treecell-cnt,
.app-menu tr.z-treeitem-seld .z-treecell-cnt,
.app-menu tr.z-treerow-seld .z-treecell-cnt,
.app-menu .z-treerow-selected .z-treecell-cnt,
.app-menu .z-treerow-seld .z-treecell-cnt {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    background-color: #efb026 !important;
    color: #11181d !important;
    box-shadow: inset 0 0 0 1px rgba(209, 138, 20, 0.45) !important;
    font-weight: 700 !important;
}

.app-menu tr.z-tree-row-seld,
.app-menu tr.z-tree-row-over-seld,
.app-sidepanel .app-menu tr.z-tree-row-seld,
.app-sidepanel .app-menu tr.z-tree-row-over-seld {
    background: transparent !important;
    background-color: transparent !important;
    border: 1px solid transparent !important;
}

.app-menu tr.z-tree-row-seld td.menu-tree-cell,
.app-menu tr.z-tree-row-over-seld td.menu-tree-cell,
.app-sidepanel .app-menu tr.z-tree-row-seld td.menu-tree-cell,
.app-sidepanel .app-menu tr.z-tree-row-over-seld td.menu-tree-cell {
    background: transparent !important;
    background-color: transparent !important;
}

.app-menu tr.z-tree-row-seld td.menu-tree-cell div.menu-tree-cell-cnt,
.app-menu tr.z-tree-row-over-seld td.menu-tree-cell div.menu-tree-cell-cnt,
.app-sidepanel .app-menu tr.z-tree-row-seld td.menu-tree-cell div.menu-tree-cell-cnt,
.app-sidepanel .app-menu tr.z-tree-row-over-seld td.menu-tree-cell div.menu-tree-cell-cnt {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    color: #11181d !important;
    box-shadow: inset 0 0 0 1px rgba(209, 138, 20, 0.45) !important;
    font-weight: 700 !important;
}

.app-menu td.z-tree-row-focus,
.app-sidepanel .app-menu td.z-tree-row-focus {
    background-image: none !important;
    background: transparent !important;
    background-color: transparent !important;
}

.app-menu tr.z-tree-row-seld td.z-tree-row-focus .z-tree-cell-cnt,
.app-menu tr.z-tree-row-over-seld td.z-tree-row-focus .z-tree-cell-cnt,
.app-sidepanel .app-menu tr.z-tree-row-seld td.z-tree-row-focus .z-tree-cell-cnt,
.app-sidepanel .app-menu tr.z-tree-row-over-seld td.z-tree-row-focus .z-tree-cell-cnt {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    color: #11181d !important;
    box-shadow: inset 0 0 0 1px rgba(209, 138, 20, 0.45) !important;
    font-weight: 700 !important;
}

/* Tree row exact ZK selectors */
#mnuMain tr.z-tree-row-seld,
#mnuMain tr.z-tree-row-over,
#mnuMain tr.z-tree-row-over-seld,
.app-menu-tree tr.z-tree-row-seld,
.app-menu-tree tr.z-tree-row-over,
.app-menu-tree tr.z-tree-row-over-seld {
    background: transparent !important;
    background-color: transparent !important;
    border: 1px solid transparent !important;
}

#mnuMain td.z-tree-row-focus,
.app-menu-tree td.z-tree-row-focus {
    background: transparent !important;
    background-color: transparent !important;
    background-image: none !important;
    border: 0 !important;
}

#mnuMain tr.z-tree-row-seld td.z-tree-cell > div.z-tree-cell-cnt,
#mnuMain tr.z-tree-row-over td.z-tree-cell > div.z-tree-cell-cnt,
#mnuMain tr.z-tree-row-over-seld td.z-tree-cell > div.z-tree-cell-cnt,
.app-menu-tree tr.z-tree-row-seld td.z-tree-cell > div.z-tree-cell-cnt,
.app-menu-tree tr.z-tree-row-over td.z-tree-cell > div.z-tree-cell-cnt,
.app-menu-tree tr.z-tree-row-over-seld td.z-tree-cell > div.z-tree-cell-cnt {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    background-color: #efb026 !important;
    color: #11181d !important;
    box-shadow: inset 0 0 0 1px rgba(209, 138, 20, 0.45) !important;
    font-weight: 700 !important;
}

/* Menu header + collapse button */
.app-shell-menu-region .z-west-header {
    font-family: var(--ly-font-sans) !important;
    font-size: 14px !important;
    font-weight: 700 !important;
    color: var(--ly-primary) !important;
    letter-spacing: 0.01em !important;
}

.app-shell-menu-region .z-border-layout-icon.z-west-colps,
.app-shell-menu-region .z-border-layout-icon.z-west-exp {
    width: 22px !important;
    height: 22px !important;
    border-radius: 8px !important;
    border: 1px solid rgba(209, 138, 20, 0.32) !important;
    background: linear-gradient(180deg, #fff8e8 0%, #f2e4bf 100%) !important;
    box-shadow: 0 2px 8px rgba(17, 24, 29, 0.08) !important;
    background-position: center center !important;
    background-image: none !important;
    position: relative !important;
}

.app-shell-menu-region .z-border-layout-icon.z-west-colps:before,
.z-west-colpsd .z-border-layout-icon.z-west-exp:before {
    position: absolute;
    inset: 0;
    text-align: center;
    line-height: 20px;
    font-family: var(--ly-font-sans);
    font-size: 13px;
    font-weight: 700;
    color: #d78b0e;
}

.app-shell-menu-region .z-border-layout-icon.z-west-colps:before {
    content: "<";
}

.z-west-colpsd .z-border-layout-icon.z-west-exp {
    width: 22px !important;
    height: 22px !important;
    border-radius: 8px !important;
    border: 1px solid rgba(209, 138, 20, 0.32) !important;
    background: linear-gradient(180deg, #fff8e8 0%, #f2e4bf 100%) !important;
    box-shadow: 0 2px 8px rgba(17, 24, 29, 0.08) !important;
    background-image: none !important;
    position: relative !important;
}

.z-west-colpsd .z-border-layout-icon.z-west-exp:before {
    content: ">";
}

/* Top bar refinement */
.desktop-tabbox > .z-tabs .z-tabs-cnt,
.desktop-tabbox > .z-tabs-scroll .z-tabs-cnt {
    text-align: left !important;
    padding: 0 0 0 14px !important;
}

.desktop-tabbox > .z-tabs .z-tabs-cnt li,
.desktop-tabbox > .z-tabs-scroll .z-tabs-cnt li {
    float: left !important;
    display: block !important;
    vertical-align: top !important;
}

.desktop-tabbox > .z-tabs .z-tabs-space,
.desktop-tabbox > .z-tabs-scroll .z-tabs-space,
.desktop-tabbox > .z-tabs .z-toolbar-tabs-outer,
.desktop-tabbox > .z-tabs-scroll .z-toolbar-tabs-outer {
    background: transparent !important;
    border: none !important;
}

.desktop-tabbox .z-tab .z-tab-hl,
.desktop-tabbox .z-tab .z-tab-hr,
.desktop-tabbox .z-tab .z-tab-hm {
    background-image: none !important;
}

.desktop-tabbox .z-tab .z-tab-hl,
.desktop-tabbox .z-tab .z-tab-hr {
    margin: 0 !important;
    padding: 0 !important;
}

.desktop-tabbox .z-tab .z-tab-hm,
.desktop-tabbox .z-tab .z-tab-hm-close {
    background: rgba(255, 255, 255, 0.06) !important;
    border-radius: 14px 14px 0 0 !important;
    box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.08) !important;
    min-height: 38px !important;
}

.desktop-tabbox .z-tab .z-tab-hl .z-tab-text,
.desktop-tabbox .z-tab .z-tab-hl:hover .z-tab-text {
    display: block !important;
    padding: 11px 18px 11px 18px !important;
    background: transparent !important;
    color: rgba(255, 255, 255, 0.78) !important;
    border-radius: 0 !important;
    box-shadow: none !important;
    position: relative !important;
}

.desktop-tabbox .z-tab-seld .z-tab-hl .z-tab-text,
.desktop-tabbox .z-tab-seld .z-tab-hl:hover .z-tab-text {
    color: #11181d !important;
}

.desktop-tabbox .z-tab-seld .z-tab-hm,
.desktop-tabbox .z-tab-seld .z-tab-hm-close {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    box-shadow: inset 0 0 0 1px rgba(209, 138, 20, 0.45), 0 8px 18px rgba(11, 15, 18, 0.18) !important;
}

.window-container-toolbar-btn,
.window-container-toolbar-btn.tab-list {
    background: #fff8e8 !important;
    color: #11181d !important;
    border: 1px solid rgba(209, 138, 20, 0.32) !important;
    box-shadow: 0 4px 12px rgba(17, 24, 29, 0.10) !important;
}

.window-container-toolbar-btn img,
.window-container-toolbar-btn.tab-list img {
    background: transparent !important;
    box-shadow: none !important;
    filter: brightness(0) saturate(100%) !important;
}

.desktop-tabbox > .z-tabs,
.desktop-tabbox > .z-tabs-scroll,
.desktop-tabbox > .z-tabs > .z-tabs-content,
.desktop-tabbox > .z-tabs > .z-tabs-cnt,
.z-tabs-scroll,
.z-tabs-scroll .z-tabs-cnt {
    min-height: 0 !important;
}

.desktop-tabbox > .z-tabs {
    padding-top: 14px !important;
    padding-bottom: 0 !important;
}

.desktop-tabbox .z-tab {
    margin-right: 0 !important;
}

.desktop-tabbox .z-tab .z-tab-hm-close,
.desktop-tabbox .z-tab-seld .z-tab-hm-close {
    padding-right: 26px !important;
    padding-left: 14px !important;
}

.desktop-tabbox .z-tab-close {
    left: auto !important;
    right: 9px !important;
    top: 50% !important;
    margin-top: -8px !important;
    width: 16px !important;
    height: 16px !important;
    background-image: none !important;
}

.desktop-tabbox .z-tab-close:before {
    color: #f8fbfc !important;
    font-size: 14px !important;
    line-height: 16px !important;
}

.desktop-tabbox .z-tab-seld .z-tab-close:before {
    color: #11181d !important;
}

.desktop-tabbox .z-toolbar-tabs,
.desktop-tabbox .z-toolbar-tabs-outer {
    margin: 0 !important;
    padding: 0 14px 0 0 !important;
    background: linear-gradient(180deg, #1b2329 0%, #10161a 100%) !important;
    border: none !important;
}

.desktop-tabbox .z-toolbar-tabs {
    top: calc(50% + 28px) !important;
    transform: translateY(-50%) !important;
}

.desktop-tabbox .z-tab + .z-tab {
    margin-left: -2px !important;
}

.window-container-toolbar-btn,
.window-container-toolbar-btn.tab-list {
    display: inline-flex !important;
    align-items: center !important;
    justify-content: center !important;
    vertical-align: middle !important;
    margin-top: 0 !important;
    margin-bottom: 0 !important;
}

/* Window list popup */
.z-menu-popup,
.z-menu-popup * {
    font-family: "Segoe UI", "Open Sans", Tahoma, Arial, sans-serif !important;
}

.z-menu-popup {
    background: #fffdfa !important;
    border: 1px solid #d7c8af !important;
    box-shadow: 0 16px 28px rgba(13, 20, 25, 0.14) !important;
    padding: 4px !important;
}

.z-menu-popup .z-menu-popup-cnt {
    background: transparent !important;
}

.z-menu-popup .z-menu-item > .z-menu-item-cnt {
    display: block !important;
    padding: 4px 8px !important;
    color: var(--ly-text) !important;
    background: transparent !important;
    border: 1px solid transparent !important;
    font-size: 14px !important;
}

.z-menu-popup .z-menu-item-over > .z-menu-item-cnt {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    background-image: none !important;
    color: #13191d !important;
    border: 1px solid #d18a14 !important;
    font-weight: 700 !important;
}

.z-menu-popup .z-menu-item-over,
.z-menu-popup .z-menu-item:hover,
.z-menu-popup .z-menu-item-over td,
.z-menu-popup .z-menu-item:hover td,
.z-menu-popup .z-menu-item-over a.z-menu-item-cnt,
.z-menu-popup .z-menu-item:hover > a.z-menu-item-cnt,
.z-menu-popup .z-menu-item > a.z-menu-item-cnt:hover {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    background-image: none !important;
    color: #13191d !important;
    border-color: #d18a14 !important;
    outline: none !important;
    box-shadow: none !important;
}

.z-menu-popup li.selected.z-menu-item > .z-menu-item-cnt,
.z-menu-popup li.selected.z-menu-item-over > .z-menu-item-cnt {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    background-image: none !important;
    color: #13191d !important;
    border: 1px solid #d18a14 !important;
    font-weight: 700 !important;
}

.z-menu-popup li.selected.z-menu-item > .z-menu-item-cnt:hover,
.z-menu-popup li.selected.z-menu-item-over > .z-menu-item-cnt:hover,
.z-menu-popup li.selected.z-menu-item > a.z-menu-item-cnt:hover,
.z-menu-popup li.selected.z-menu-item-over > a.z-menu-item-cnt:hover {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    background-image: none !important;
    color: #13191d !important;
    border: 1px solid #d18a14 !important;
}

/* Status bar */
.app-statusbar {
    background: linear-gradient(180deg, #1b2329 0%, #10161a 100%) !important;
    border-top: 1px solid rgba(255, 255, 255, 0.08) !important;
    color: #f8fbfc !important;
    font-family: var(--ly-font-sans) !important;
}

.app-statusbar-main,
.app-statusbar-meta,
.app-statusbar .z-label,
.app-statusbar .status-db,
.app-statusbar .status-info {
    color: rgba(248, 251, 252, 0.92) !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 13px !important;
}

.app-statusbar .status-db {
    font-weight: 700 !important;
}

.app-statusbar .status-db:hover {
    color: #f2b52f !important;
}

/* Record info dialog */
.record-info-window .z-window-modal-header,
.record-info-window .z-window-popup-header,
.record-info-window .z-window-highlighted-header,
.record-info-window .z-window-overlapped-header {
    background: linear-gradient(180deg, #1b2329 0%, #10161a 100%) !important;
    color: #f8fbfc !important;
    font-family: var(--ly-font-sans) !important;
    border-bottom: 1px solid rgba(255, 255, 255, 0.08) !important;
}

.record-info-window .z-window-modal-cnt,
.record-info-window .z-window-highlighted-cnt,
.record-info-window .z-window-overlapped-cnt,
.record-info-window .z-window-popup-cnt {
    background: #fffdfa !important;
    border-color: #d7c8af !important;
    border-radius: 0 !important;
}

.record-info-content,
.record-info-table,
.record-info-window .z-listbox,
.record-info-window .z-listbox-body,
.record-info-window .z-listcell-cnt,
.record-info-window .z-center,
.record-info-window .z-center-body,
.record-info-window .z-center-cnt,
.record-info-window .z-window-modal-cl,
.record-info-window .z-window-modal-cr,
.record-info-window .z-window-modal-bl,
.record-info-window .z-window-modal-br,
.record-info-window .z-window-highlighted-cl,
.record-info-window .z-window-highlighted-cr,
.record-info-window .z-window-highlighted-bl,
.record-info-window .z-window-highlighted-br,
.record-info-window .z-window-overlapped-cl,
.record-info-window .z-window-overlapped-cr,
.record-info-window .z-window-overlapped-bl,
.record-info-window .z-window-overlapped-br {
    background: #fffdfa !important;
    color: #1b2329 !important;
    font-family: var(--ly-font-sans) !important;
    border-radius: 0 !important;
}

.record-info-pre {
    margin: 0 !important;
    padding: 14px 16px !important;
    background: #fffdfa !important;
    color: #1b2329 !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 13px !important;
    line-height: 1.5 !important;
}

.record-info-actions {
    background: linear-gradient(180deg, rgba(255, 253, 250, 0.98) 0%, rgba(244, 238, 229, 0.98) 100%) !important;
    border-top: 1px solid rgba(22, 32, 39, 0.10) !important;
    padding: 12px 16px !important;
    text-align: right !important;
}

.record-info-actions .z-button-cm,
.record-info-actions .z-button-bm,
.record-info-actions .z-button-bl,
.record-info-actions .z-button-br,
.record-info-actions .z-button-tm,
.record-info-actions .z-button-tl,
.record-info-actions .z-button-tr,
.record-info-actions .z-button-cl,
.record-info-actions .z-button-cr {
    background: transparent !important;
    box-shadow: none !important;
}

.record-info-button .z-button-cm {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    color: #11181d !important;
    border: 1px solid #d18a14 !important;
    border-radius: 12px !important;
    font-family: var(--ly-font-sans) !important;
    font-weight: 700 !important;
    min-height: 42px !important;
    min-width: 168px !important;
    padding: 0 18px !important;
    text-align: center !important;
}

.record-info-button .z-button-cm:before {
    content: none !important;
    display: none !important;
}

.record-info-window .z-window-close,
.record-info-window .z-window-modal-close,
.record-info-window .z-window-highlighted-close,
.record-info-window .z-window-overlapped-close,
.record-info-window .z-window-popup-close,
.record-info-window a.z-window-close,
.record-info-window a.z-window-modal-close,
.record-info-window a.z-window-highlighted-close,
.record-info-window a.z-window-overlapped-close,
.record-info-window a.z-window-popup-close,
.record-info-window [id$="close"] {
    background: linear-gradient(180deg, #1b2329 0%, #10161a 100%) !important;
    background-image: none !important;
    border: none !important;
    border-radius: 0 !important;
    width: 32px !important;
    min-width: 32px !important;
    height: 32px !important;
    top: 0 !important;
    right: 0 !important;
    color: #f8fbfc !important;
    font-size: 0 !important;
    line-height: 32px !important;
    text-align: center !important;
    text-indent: 0 !important;
    overflow: hidden !important;
}

.record-info-window .z-window-close:before,
.record-info-window .z-window-modal-close:before,
.record-info-window .z-window-highlighted-close:before,
.record-info-window .z-window-overlapped-close:before,
.record-info-window .z-window-popup-close:before,
.record-info-window a.z-window-close:before,
.record-info-window a.z-window-modal-close:before,
.record-info-window a.z-window-highlighted-close:before,
.record-info-window a.z-window-overlapped-close:before,
.record-info-window a.z-window-popup-close:before {
    content: "×" !important;
    display: block !important;
    color: #f8fbfc !important;
    font-size: 20px !important;
    font-weight: 700 !important;
    line-height: 32px !important;
    text-align: center !important;
    background: transparent !important;
}

/* Form groups and combobox alignment */
td.z-group-inner {
    padding: 12px 10px 6px !important;
    background: transparent !important;
}

.z-group-inner .z-group-cnt,
.z-group-inner .z-group-cnt span {
    display: inline-block !important;
    width: auto !important;
    max-width: calc(100% - 18px) !important;
    padding: 0 0 0 4px !important;
    background: transparent !important;
    color: var(--ly-primary) !important;
    border-radius: 0 !important;
    box-shadow: none !important;
    vertical-align: middle !important;
    white-space: nowrap !important;
}

.z-group-img {
    width: 12px !important;
    min-width: 12px !important;
    height: 12px !important;
    background-image: url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='10' height='10' viewBox='0 0 10 10'%3E%3Cpath fill='none' stroke='%23d78b0e' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.8' d='M2 3l3 4 3-4'/%3E%3C/svg%3E\") !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 10px 10px !important;
    filter: none !important;
    vertical-align: middle !important;
}

tr.z-group td,
tbody tr.z-group td {
    border-top: 1px solid rgba(239, 176, 38, 0.24) !important;
    border-bottom: 1px solid rgba(239, 176, 38, 0.24) !important;
}

.z-combobox,
.z-combobox table {
    height: 32px !important;
    border-collapse: collapse !important;
}

.z-combobox table td:first-child,
.z-combobox table td:last-child,
.z-combobox .z-combobox-btn,
.z-combobox .z-combobox-img,
.z-combobox-inp {
    height: 32px !important;
    box-sizing: border-box !important;
}

.z-combobox-inp {
    line-height: 22px !important;
    padding-top: 3px !important;
    padding-bottom: 3px !important;
    border-radius: 10px 0 0 10px !important;
    border-right: none !important;
}

/* Final form button fixes */
td.editor-button {
    width: 40px !important;
    min-width: 40px !important;
    max-width: 40px !important;
}

button.editor-button,
.editor-button.z-button {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
}

button.editor-button .z-button-cm,
.editor-button.z-button .z-button-cm {
    background-image: url("../../../images/Calculator10.png") !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 14px 14px !important;
    text-indent: -9999px !important;
    overflow: hidden !important;
}

button.editor-button img,
.editor-button.z-button img,
.editor-button .z-button-cm img {
    display: none !important;
}

/* Dynamic window grid mode: selected row + pagination */
.adtab-grid-panel .z-grid-body tr.z-row[style*="#C4DCFB"],
.adtab-grid-panel .z-grid-body tr.z-row[style*="#c4dcfb"] {
    background: #efddb0 !important;
}

.adtab-grid-panel .z-grid-body tr.z-row[style*="#C4DCFB"] > td.z-row-inner,
.adtab-grid-panel .z-grid-body tr.z-row[style*="#c4dcfb"] > td.z-row-inner,
.adtab-grid-panel .z-grid-body td.z-row-inner[style*="#C4DCFB"],
.adtab-grid-panel .z-grid-body td.z-row-inner[style*="#c4dcfb"] {
    background: linear-gradient(180deg, #f7ebc8 0%, #efddb0 100%) !important;
    border-top: 1px solid rgba(176, 125, 13, 0.28) !important;
    border-bottom: 1px solid rgba(176, 125, 13, 0.28) !important;
    color: #1b2329 !important;
}

.adtab-grid-panel .z-grid-body tr.z-row[style*="#C4DCFB"] .z-row-cnt,
.adtab-grid-panel .z-grid-body tr.z-row[style*="#c4dcfb"] .z-row-cnt,
.adtab-grid-panel .z-grid-body tr.z-row[style*="#C4DCFB"] .z-row-cnt *,
.adtab-grid-panel .z-grid-body tr.z-row[style*="#c4dcfb"] .z-row-cnt * {
    color: #1b2329 !important;
}

.adtab-grid-panel .z-south,
.adtab-grid-panel .z-south-body {
    background: #f3ede4 !important;
    border-top: 1px solid var(--ly-border) !important;
}

.adtab-grid-panel .z-paging {
    height: 100% !important;
    padding: 3px 10px !important;
    background: linear-gradient(180deg, #f5efe7 0%, #ede3d5 100%) !important;
    color: #1b2329 !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 13px !important;
    box-sizing: border-box !important;
    overflow: hidden !important;
}

.adtab-grid-panel .z-paging > table {
    float: left;
}

.adtab-grid-panel .z-paging > table td {
    padding: 0 2px;
    vertical-align: middle;
}

.adtab-grid-panel .z-paging .z-paging-sep {
    display: inline-block;
    width: 1px;
    height: 16px;
    margin: 0 6px;
    background: rgba(27, 35, 41, 0.12);
    vertical-align: middle;
}

.adtab-grid-panel .z-paging .z-paging-text,
.adtab-grid-panel .z-paging .z-paging-info {
    color: #4e5a62 !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 13px !important;
}

.adtab-grid-panel .z-paging .z-paging-text {
    font-weight: 600 !important;
}

.adtab-grid-panel .z-paging .z-paging-info {
    float: right;
    margin-top: 4px;
    color: #1b2329 !important;
    font-weight: 600 !important;
}

.adtab-grid-panel .z-paging .z-paging-inp {
    width: 40px !important;
    height: 24px !important;
    padding: 2px 6px !important;
    border: 1px solid var(--ly-border-strong) !important;
    border-radius: 7px !important;
    background: #fffdfa !important;
    color: #1b2329 !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 13px !important;
    font-weight: 600 !important;
    text-align: center !important;
    box-sizing: border-box !important;
    box-shadow: none !important;
}

.adtab-grid-panel .z-paging .z-paging-inp:focus {
    border-color: #b07d0d !important;
    box-shadow: 0 0 0 2px rgba(176, 125, 13, 0.14) !important;
}

.adtab-grid-panel .z-paging .z-paging-btn {
    border-collapse: separate !important;
    border-spacing: 0 !important;
    background: transparent !important;
}

.adtab-grid-panel .z-paging .z-paging-btn button {
    width: 24px !important;
    height: 24px !important;
    min-width: 24px !important;
    min-height: 24px !important;
    border: 1px solid var(--ly-border-strong) !important;
    border-radius: 7px !important;
    background-color: #fffdfa !important;
    background-position: center center !important;
    background-repeat: no-repeat !important;
    background-size: 12px 12px !important;
    box-shadow: none !important;
    cursor: pointer !important;
}

.adtab-grid-panel .z-paging .z-paging-btn button:hover {
    background-color: #f0e2bf !important;
    border-color: #b07d0d !important;
}

.adtab-grid-panel .z-paging .z-paging-btn-disd button,
.adtab-grid-panel .z-paging .z-paging-btn button:disabled {
    background-color: #f7f2ea !important;
    border-color: #d9d1c4 !important;
    opacity: 0.45 !important;
    cursor: default !important;
}

.adtab-grid-panel .z-paging .z-paging-first {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath d='M8 2.5L4.5 6 8 9.5' fill='none' stroke='%23162027' stroke-width='1.6' stroke-linecap='round' stroke-linejoin='round'/%3E%3Cpath d='M3 2.5v7' fill='none' stroke='%23162027' stroke-width='1.6' stroke-linecap='round'/%3E%3C/svg%3E") !important;
}

.adtab-grid-panel .z-paging .z-paging-prev {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath d='M7.5 2.5L4 6l3.5 3.5' fill='none' stroke='%23162027' stroke-width='1.6' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E") !important;
}

.adtab-grid-panel .z-paging .z-paging-next {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath d='M4.5 2.5L8 6 4.5 9.5' fill='none' stroke='%23162027' stroke-width='1.6' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E") !important;
}

.adtab-grid-panel .z-paging .z-paging-last {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath d='M4 2.5L7.5 6 4 9.5' fill='none' stroke='%23162027' stroke-width='1.6' stroke-linecap='round' stroke-linejoin='round'/%3E%3Cpath d='M9 2.5v7' fill='none' stroke='%23162027' stroke-width='1.6' stroke-linecap='round'/%3E%3C/svg%3E") !important;
}

/* Payment/receipt invoice table */
.payment-invoice-grid,
.payment-invoice-grid.z-grid,
.payment-invoice-grid .z-grid-header,
.payment-invoice-grid .z-grid-body {
    background: #fffdfa !important;
    border-color: var(--ly-border) !important;
}

.payment-invoice-grid {
    border: 1px solid var(--ly-border) !important;
    border-radius: 12px !important;
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.65) !important;
    overflow: hidden !important;
}

.payment-invoice-grid .z-grid-header {
    border-bottom: 1px solid var(--ly-border) !important;
}

.payment-invoice-grid .z-columns,
.payment-invoice-grid .z-column,
.payment-invoice-grid .z-column-cnt {
    background: #f3ede4 !important;
    background-image: none !important;
    color: #31404a !important;
    border: none !important;
    box-shadow: none !important;
}

.payment-invoice-grid .z-column {
    border-right: 1px solid rgba(194, 181, 159, 0.45) !important;
}

.payment-invoice-grid .z-column:last-child {
    border-right: none !important;
}

.payment-invoice-grid .z-column-cnt {
    padding: 10px 12px !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 12px !important;
    font-weight: 700 !important;
    letter-spacing: 0.01em !important;
    white-space: nowrap !important;
}

.payment-invoice-grid .z-grid-body tr.z-row,
.payment-invoice-grid .z-grid-body tr.z-row td.z-row-inner {
    background: #fffdfa !important;
}

.payment-invoice-grid .z-grid-body tr.z-grid-odd,
.payment-invoice-grid .z-grid-body tr.z-grid-odd td.z-row-inner {
    background: #f8f3ea !important;
}

.payment-invoice-grid .z-grid-body tr.z-row:hover,
.payment-invoice-grid .z-grid-body tr.z-row:hover td.z-row-inner {
    background: #f3ead7 !important;
}

.payment-invoice-grid .z-grid-body tr.z-row td.z-row-inner {
    padding: 0 !important;
    border-top: none !important;
    border-right: none !important;
    border-bottom: 1px solid rgba(217, 209, 196, 0.7) !important;
}

.payment-invoice-grid .z-grid-body .z-row-cnt {
    padding: 12px 12px !important;
    color: #1b2329 !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 13px !important;
    background: transparent !important;
}

.payment-invoice-grid .z-grid-body .z-label,
.payment-invoice-grid .z-grid-body .z-row-cnt label {
    color: #1b2329 !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 13px !important;
}

.payment-invoice-grid .z-grid-body .z-textbox,
.payment-invoice-grid .z-grid-body .z-textbox-readonly {
    height: auto !important;
    min-height: 0 !important;
    padding: 0 8px 0 0 !important;
    margin: 0 !important;
    border: none !important;
    background: transparent !important;
    color: #1b2329 !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 13px !important;
    box-shadow: none !important;
}

.payment-invoice-grid .z-grid-body .z-textbox:focus,
.payment-invoice-grid .z-grid-body .z-textbox-readonly:focus {
    box-shadow: none !important;
}

.receipt-pos-field,
.receipt-orgcharge-field,
.receipt-amount-field {
    max-width: 160px !important;
    margin-left: 8px !important;
}

.receipt-pos-field {
    max-width: 140px !important;
}

/* Top form layout for receipts/payment orders */
.payment-top-fields-grid,
.receipt-top-fields-grid,
.payment-top-fields-grid .z-grid-body,
.receipt-top-fields-grid .z-grid-body {
    width: 100% !important;
    background: transparent !important;
    border: none !important;
}

.payment-top-fields-grid .z-grid-body > table,
.receipt-top-fields-grid .z-grid-body > table {
    width: 100% !important;
    table-layout: fixed !important;
    border-collapse: separate !important;
    border-spacing: 0 12px !important;
}

.payment-top-fields-grid .z-row,
.receipt-top-fields-grid .z-row,
.payment-top-fields-grid .z-row-inner,
.receipt-top-fields-grid .z-row-inner {
    background: transparent !important;
    border: none !important;
}

.payment-top-fields-grid .z-row-inner,
.receipt-top-fields-grid .z-row-inner {
    padding: 0 !important;
    vertical-align: middle !important;
}

.payment-top-fields-grid .z-row-inner:nth-child(1),
.payment-top-fields-grid .z-row-inner:nth-child(4),
.receipt-top-fields-grid .z-row-inner:nth-child(1),
.receipt-top-fields-grid .z-row-inner:nth-child(4) {
    width: 9% !important;
}

.payment-top-fields-grid .z-row-inner:nth-child(2),
.payment-top-fields-grid .z-row-inner:nth-child(5),
.receipt-top-fields-grid .z-row-inner:nth-child(2),
.receipt-top-fields-grid .z-row-inner:nth-child(5) {
    width: 38% !important;
}

.payment-top-fields-grid .z-row-inner:nth-child(3),
.payment-top-fields-grid .z-row-inner:nth-child(6),
.receipt-top-fields-grid .z-row-inner:nth-child(3),
.receipt-top-fields-grid .z-row-inner:nth-child(6) {
    width: 3% !important;
}

.payment-top-fields-grid .z-row-cnt,
.receipt-top-fields-grid .z-row-cnt {
    padding: 0 !important;
    overflow: visible !important;
}

.payment-top-fields-grid .z-label,
.receipt-top-fields-grid .z-label {
    color: #1b2329 !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 12px !important;
    font-weight: 700 !important;
    letter-spacing: 0.01em !important;
}

.payment-top-fields-grid .z-textbox,
.payment-top-fields-grid .z-combobox,
.payment-top-fields-grid .z-datebox,
.payment-top-fields-grid .lookup-editor-box,
.receipt-top-fields-grid .z-textbox,
.receipt-top-fields-grid .z-combobox,
.receipt-top-fields-grid .z-datebox,
.receipt-top-fields-grid .lookup-editor-box {
    width: 100% !important;
    max-width: none !important;
}

.payment-top-fields-grid .lookup-editor-box > table,
.receipt-top-fields-grid .lookup-editor-box > table {
    width: 100% !important;
}

.payment-top-fields-grid .z-separator-ver,
.receipt-top-fields-grid .z-separator-ver {
    display: block !important;
    width: 100% !important;
    min-width: 18px !important;
}

/* Payment tab layout and accordion */
.payment-rule-tab-content {
    padding-top: 8px;
    height: 100% !important;
    overflow-y: auto !important;
    overflow-x: hidden !important;
    padding-right: 8px !important;
}

.payment-rule-layout {
    border-collapse: separate !important;
    border-spacing: 18px 16px !important;
    table-layout: fixed !important;
}

.payment-rule-layout > tbody > tr > td.z-table-children {
    vertical-align: top !important;
}

.payment-rule-layout-main > .z-panel > .z-panel-body,
.payment-rule-layout-summary > .z-panel > .z-panel-body,
.payment-rule-layout-tabs > .z-panel > .z-panel-body,
.payment-rule-layout-footer > .z-panel > .z-panel-body,
.payment-rule-layout-footer > .z-panel > .z-panel-body > .z-panel-children {
    padding: 0 !important;
    background: transparent !important;
    border: none !important;
}

.payment-summary-panel > .z-panel-body {
    padding: 18px !important;
    background: #fffdfa !important;
    border: 1px solid var(--ly-border) !important;
    border-radius: 18px !important;
    box-shadow: 0 10px 24px rgba(18, 27, 34, 0.08) !important;
}

.payment-summary-tree,
.payment-summary-tree .z-tree-body {
    background: #fffdfa !important;
    border: none !important;
}

.payment-summary-tree .z-tree-body {
    width: 100% !important;
    border-radius: 12px !important;
    box-shadow: inset 0 0 0 1px rgba(194, 181, 159, 0.45) !important;
}

.payment-summary-tree .z-tree-row {
    background: #fffdfa !important;
}

.payment-summary-tree .z-tree-cell-cnt {
    padding: 10px 14px !important;
    color: #1b2329 !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 13px !important;
}

.payment-mediums-accordion,
.payment-mediums-accordion.z-tabbox-accordion,
.payment-mediums-accordion .z-tabpanels-accordion,
.payment-mediums-accordion .z-tabpanel-accordion-outer,
.payment-mediums-accordion .z-tabpanel-accordion {
    height: auto !important;
}

.payment-mediums-accordion,
.payment-mediums-accordion .z-tabpanels-accordion {
    background: transparent !important;
}

.payment-mediums-accordion .z-tabpanel-accordion-outer {
    margin: 0 0 12px !important;
    border: 1px solid var(--ly-border) !important;
    border-radius: 18px !important;
    background: #fffdfa !important;
    box-shadow: 0 10px 24px rgba(18, 27, 34, 0.08) !important;
    overflow: hidden !important;
}

.payment-mediums-accordion .z-tab-accordion .z-tab-accordion-header,
.payment-mediums-accordion .z-tab-accordion .z-tab-accordion-tl,
.payment-mediums-accordion .z-tab-accordion .z-tab-accordion-tr,
.payment-mediums-accordion .z-tab-accordion .z-tab-accordion-hl,
.payment-mediums-accordion .z-tab-accordion .z-tab-accordion-hr,
.payment-mediums-accordion .z-tab-accordion .z-tab-accordion-hm {
    background: transparent !important;
    border: none !important;
    box-shadow: none !important;
}

.payment-mediums-accordion .z-tab-accordion .z-tab-accordion-hm {
    display: flex !important;
    align-items: center !important;
    min-height: 48px !important;
    padding: 0 18px !important;
    color: #31404a !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 13px !important;
    font-weight: 700 !important;
    letter-spacing: 0.01em !important;
    border-bottom: 1px solid rgba(194, 181, 159, 0.35) !important;
}

.payment-mediums-accordion .z-tab-accordion-seld .z-tab-accordion-hm {
    background: linear-gradient(180deg, #ffbf28 0%, #e9a311 100%) !important;
    color: #1b2329 !important;
    border-bottom-color: #c3890d !important;
}

.payment-mediums-accordion .z-tab-accordion-disd .z-tab-accordion-hm {
    color: #6f7b82 !important;
    background: #f4ecde !important;
}

.payment-mediums-accordion .z-tab-accordion-text {
    font: inherit !important;
    color: inherit !important;
}

.payment-mediums-accordion .z-tabpanel-accordion {
    padding: 18px 20px 20px !important;
    overflow: visible !important;
    background: #fffdfa !important;
}

.payment-mediums-accordion .z-grid,
.payment-mediums-accordion .z-grid-body,
.payment-mediums-accordion .z-grid-body > table {
    width: 100% !important;
}

.payment-mediums-accordion .z-grid,
.payment-mediums-accordion .z-grid-body,
.payment-mediums-accordion .z-grid-body > table,
.payment-mediums-accordion .z-row,
.payment-mediums-accordion .z-row-inner {
    background: transparent !important;
    border: none !important;
}

.payment-mediums-accordion .z-grid-body > table {
    table-layout: fixed !important;
}

.payment-mediums-accordion .z-row-inner {
    padding: 0 14px 14px 0 !important;
    vertical-align: top !important;
}

.payment-mediums-accordion .z-row-inner:nth-child(1),
.payment-mediums-accordion .z-row-inner:nth-child(3) {
    width: 18% !important;
    min-width: 150px !important;
}

.payment-mediums-accordion .z-row-inner:last-child {
    padding-right: 0 !important;
}

.payment-mediums-accordion .z-row-cnt {
    padding: 0 !important;
    overflow: visible !important;
}

.payment-mediums-accordion .z-label {
    color: #31404a !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 12px !important;
    font-weight: 700 !important;
    letter-spacing: 0.01em !important;
}

.payment-mediums-accordion .z-textbox,
.payment-mediums-accordion .z-combobox,
.payment-mediums-accordion .z-datebox,
.payment-mediums-accordion .lookup-editor-box,
.payment-mediums-accordion .z-bandbox {
    width: 100% !important;
    max-width: none !important;
}

.payment-mediums-accordion .lookup-editor-box > table {
    width: 100% !important;
}

.payment-process-bar {
    padding-top: 8px !important;
}

.payment-command-button {
    display: inline-block !important;
    margin: 12px 10px 0 0 !important;
}

.payment-command-button:last-child {
    margin-right: 0 !important;
}

.payment-command-button > table.z-button {
    border-collapse: separate !important;
    border-spacing: 0 !important;
}

.payment-command-button > table.z-button,
.payment-command-button > table.z-button td {
    background: transparent !important;
    border: none !important;
    box-shadow: none !important;
}

.payment-command-button > table.z-button > tbody > tr:first-child,
.payment-command-button > table.z-button > tbody > tr:last-child,
.payment-command-button > table.z-button .z-button-cl,
.payment-command-button > table.z-button .z-button-cr {
    display: none !important;
}

.payment-command-button > table.z-button .z-button-cm {
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    min-width: 154px !important;
    min-height: 42px !important;
    padding: 0 22px !important;
    border: 1px solid #1d262c !important;
    border-radius: 999px !important;
    background: #1d262c !important;
    color: #fff7e8 !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 14px !important;
    font-weight: 700 !important;
    text-align: center !important;
    white-space: nowrap !important;
    letter-spacing: 0.01em !important;
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.08) !important;
}

.payment-command-button-primary > table.z-button .z-button-cm {
    border-color: #bb7e06 !important;
    background: linear-gradient(180deg, #ffbf28 0%, #e39a08 100%) !important;
    color: #1d1a14 !important;
    box-shadow: inset 0 1px 0 rgba(255, 248, 220, 0.5) !important;
}

.payment-command-button-large > table.z-button .z-button-cm {
    min-width: 188px !important;
    min-height: 46px !important;
}

.payment-command-button:hover > table.z-button .z-button-cm {
    filter: brightness(1.04) !important;
}

/* Final intbox/numberbox button polish */
.number-editor-box > table > tbody > tr > td.number-editor-cell,
.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button,
.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button > table.number-editor-button.z-button {
    border-radius: 0 !important;
    background: transparent !important;
    box-shadow: none !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button > table.number-editor-button.z-button .z-button-cm {
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    padding: 0 !important;
    margin: 0 !important;
    line-height: 0 !important;
    border-top-left-radius: 0 !important;
    border-bottom-left-radius: 0 !important;
    border-top-right-radius: 10px !important;
    border-bottom-right-radius: 10px !important;
    background-color: #f4ede4 !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 14px 14px !important;
    box-shadow: none !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-cm {
    border: 1px solid #d8cfbf !important;
    border-left: none !important;
    border-radius: 0 10px 10px 0 !important;
    background: #f4ede4 !important;
    background-image: none !important;
    padding-top: 7px !important;
}

/* Final location dialog controls */
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div.z-grid[style*="padding:5px;"] select.z-listbox[style*="width:100%"] {
    display: block !important;
    width: 100% !important;
    height: 32px !important;
    min-height: 32px !important;
    padding: 5px 38px 5px 12px !important;
    border: 1px solid #d8cfbf !important;
    border-radius: 10px !important;
    background-color: #fffdfa !important;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8' viewBox='0 0 12 8'%3E%3Cpath fill='%23232d34' d='M1.41.59 6 5.17 10.59.59 12 2l-6 6-6-6z'/%3E%3C/svg%3E"), linear-gradient(180deg, #f4ede4 0%, #f4ede4 100%) !important;
    background-repeat: no-repeat !important;
    background-position: right 14px center, right top !important;
    background-size: 12px 8px, 40px 100% !important;
    box-sizing: border-box !important;
    color: #1b2329 !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 14px !important;
    line-height: 20px !important;
    appearance: none !important;
    -webkit-appearance: none !important;
    -moz-appearance: none !important;
    box-shadow: none !important;
    outline: none !important;
    vertical-align: middle !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div[style*="width:100%;text-align:right;"] {
    padding: 12px 14px 14px !important;
    box-sizing: border-box !important;
    background: #fffdfa !important;
    text-align: right !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div[style*="width:100%;text-align:right;"] > span.z-button > table.z-button {
    display: inline-table !important;
    margin-left: 10px !important;
    border-collapse: separate !important;
    border-spacing: 0 !important;
    border-radius: 12px !important;
    overflow: hidden !important;
    background: transparent !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div[style*="width:100%;text-align:right;"] > span.z-button > table.z-button > tbody > tr:first-child,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div[style*="width:100%;text-align:right;"] > span.z-button > table.z-button > tbody > tr:last-child,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div[style*="width:100%;text-align:right;"] > span.z-button > table.z-button .z-button-cl,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div[style*="width:100%;text-align:right;"] > span.z-button > table.z-button .z-button-cr {
    display: none !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div[style*="width:100%;text-align:right;"] > span.z-button > table.z-button .z-button-cm {
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    width: 72px !important;
    min-width: 72px !important;
    height: 40px !important;
    min-height: 40px !important;
    padding: 0 !important;
    background: #1b2329 !important;
    border-radius: 12px !important;
    box-shadow: none !important;
    background-image: none !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div[style*="width:100%;text-align:right;"] > span.z-button > table.z-button .z-button-cm img {
    display: block !important;
    width: 16px !important;
    height: 16px !important;
    margin: 0 !important;
    vertical-align: middle !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div[style*="width:100%;text-align:right;"] > span.z-button > table.z-button:has(img[src*="Ok16.png"]) .z-button-cm {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
}

/* Final adwindow-toolbar hover artifact fix */
.adwindow-toolbar a.toolbar-button,
.adwindow-toolbar a.toolbar-button-large,
.adwindow-toolbar a.embedded-toolbar-button,
.adwindow-toolbar a.z-toolbar-button {
    background: transparent !important;
    background-color: transparent !important;
    background-image: none !important;
    border: 0 !important;
    outline: none !important;
    box-shadow: none !important;
}

.adwindow-toolbar a.toolbar-button:hover,
.adwindow-toolbar a.toolbar-button:focus,
.adwindow-toolbar a.toolbar-button-large:hover,
.adwindow-toolbar a.toolbar-button-large:focus,
.adwindow-toolbar a.embedded-toolbar-button:hover,
.adwindow-toolbar a.embedded-toolbar-button:focus,
.adwindow-toolbar a.z-toolbar-button:hover,
.adwindow-toolbar a.z-toolbar-button:focus {
    background: transparent !important;
    background-color: transparent !important;
    background-image: none !important;
    border: 0 !important;
    outline: none !important;
    box-shadow: none !important;
}

.adwindow-toolbar a.toolbar-button img,
.adwindow-toolbar a.toolbar-button-large img,
.adwindow-toolbar a.embedded-toolbar-button img,
.adwindow-toolbar a.z-toolbar-button img {
    outline: none !important;
}

.adwindow-toolbar a.toolbar-button img:hover,
.adwindow-toolbar a.toolbar-button img:focus,
.adwindow-toolbar a.toolbar-button-large img:hover,
.adwindow-toolbar a.toolbar-button-large img:focus,
.adwindow-toolbar a.embedded-toolbar-button img:hover,
.adwindow-toolbar a.embedded-toolbar-button img:focus,
.adwindow-toolbar a.z-toolbar-button img:hover,
.adwindow-toolbar a.z-toolbar-button img:focus,
.adwindow-toolbar a.toolbar-button:hover img,
.adwindow-toolbar a.toolbar-button:focus img,
.adwindow-toolbar a.toolbar-button-large:hover img,
.adwindow-toolbar a.toolbar-button-large:focus img,
.adwindow-toolbar a.embedded-toolbar-button:hover img,
.adwindow-toolbar a.embedded-toolbar-button:focus img,
.adwindow-toolbar a.z-toolbar-button:hover img,
.adwindow-toolbar a.z-toolbar-button:focus img {
    outline: none !important;
    box-shadow: none !important;
}

/* Final dynamic search popup styling */
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox > .z-tabs,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox > .z-tabs-scroll,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox > .z-tabs > .z-tabs-content,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox > .z-tabs > .z-tabs-cnt,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tabs-scroll .z-tabs-cnt {
    background: linear-gradient(180deg, #1b2329 0%, #10161a 100%) !important;
    border: none !important;
    box-shadow: none !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox > .z-tabs {
    padding: 12px 14px 0 !important;
    border-radius: 0 !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tabs-space,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tabs-edge {
    background: transparent !important;
    border: none !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab {
    margin-right: 0 !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab .z-tab-hl,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab .z-tab-hr,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab .z-tab-hm,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab-seld .z-tab-hl,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab-seld .z-tab-hr,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab-seld .z-tab-hm {
    background-image: none !important;
    background: transparent !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab .z-tab-hl,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab .z-tab-hr {
    margin: 0 !important;
    padding: 0 !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab .z-tab-hm {
    background: rgba(255, 255, 255, 0.06) !important;
    border-radius: 10px 10px 0 0 !important;
    box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.08) !important;
    min-height: 38px !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab .z-tab-hl .z-tab-text,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab .z-tab-hl:hover .z-tab-text {
    display: block !important;
    padding: 11px 18px !important;
    border-radius: 0 !important;
    background: transparent !important;
    color: rgba(255, 255, 255, 0.72) !important;
    box-shadow: none !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab-seld .z-tab-hl .z-tab-text,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab-seld .z-tab-hl:hover .z-tab-text {
    color: #11181d !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab-seld .z-tab-hm {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    border-radius: 10px 10px 0 0 !important;
    box-shadow: inset 0 0 0 1px rgba(209, 138, 20, 0.45), 0 8px 18px rgba(11, 15, 18, 0.18) !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-tab + .z-tab {
    margin-left: -2px !important;
}

.z-window-modal table.action-button.z-button {
    border-collapse: collapse !important;
    border-spacing: 0 !important;
    border-radius: 10px !important;
    overflow: hidden !important;
}

.z-window-modal table.action-button.z-button > tbody > tr:first-child,
.z-window-modal table.action-button.z-button > tbody > tr:last-child,
.z-window-modal table.action-button.z-button .z-button-cl,
.z-window-modal table.action-button.z-button .z-button-cr {
    display: none !important;
}

.z-window-modal table.action-button.z-button .z-button-cm {
    display: flex !important;
    align-items: center !important;
    justify-content: center !important;
    width: 72px !important;
    min-width: 72px !important;
    height: 40px !important;
    min-height: 40px !important;
    padding: 0 !important;
    line-height: 0 !important;
    background: #1b2329 !important;
    color: #f8fbfc !important;
    box-shadow: none !important;
}

.z-window-modal table.action-button.z-button .z-button-cm img {
    display: block !important;
    width: 20px !important;
    height: 20px !important;
    margin: 0 !important;
}

.z-window-modal table.action-button.z-button:has(img[src*="Ok24.png"]) .z-button-cm {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    color: #11181d !important;
}

.z-window-modal > .z-window-modal-cl,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm,
.z-window-modal > .z-window-modal-bl,
.z-window-modal > .z-window-modal-bl > .z-window-modal-br {
    background-image: none !important;
    background-color: #fffdfa !important;
    border: 0 !important;
    box-shadow: none !important;
}

.z-window-modal > .z-window-modal-cl {
    border-radius: 0 0 14px 14px !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt {
    border-radius: 0 0 14px 14px !important;
    overflow: hidden !important;
    background: #fffdfa !important;
}

.z-window-modal .z-south-body {
    padding: 10px 14px 14px !important;
    box-sizing: border-box !important;
    background: #fffdfa !important;
}

.z-window-modal .z-south-body > table.z-hbox {
    width: 100% !important;
}

.z-window-modal .z-south-body > table.z-hbox > tbody > tr > td:first-child {
    padding-left: 0 !important;
}

/* Final lookup/location button polish */
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button > table.lookup-editor-button.z-button,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button > table.location-editor-button.z-button {
    border-radius: 0 !important;
    background: transparent !important;
    box-shadow: none !important;
}

.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button > table.lookup-editor-button.z-button .z-button-cm,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button > table.location-editor-button.z-button .z-button-cm {
    display: block !important;
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    padding: 0 !important;
    margin: 0 !important;
    border-top-left-radius: 0 !important;
    border-bottom-left-radius: 0 !important;
    border-top-right-radius: 10px !important;
    border-bottom-right-radius: 10px !important;
    background-color: #f4ede4 !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 14px 14px !important;
    box-shadow: none !important;
}

/* Final editor widgets override after legacy editor-button rules */
.number-editor-box > table,
.lookup-editor-box > table[width="100%"],
.location-editor-box > table[width="100%"] {
    width: 100% !important;
    table-layout: fixed !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
}

.number-editor-box > table > tbody > tr > td:first-child,
.lookup-editor-box > table[width="100%"] > tbody > tr > td:first-child,
.location-editor-box > table[width="100%"] > tbody > tr > td:first-child {
    width: auto !important;
    padding: 0 !important;
    margin: 0 !important;
}

.number-editor-box > table > tbody > tr > td:first-child > input.z-decimalbox,
.number-editor-box > table > tbody > tr > td:first-child > input.z-intbox,
.number-editor-box > table > tbody > tr > td:first-child > input.z-longbox,
.number-editor-box > table > tbody > tr > td:first-child > input.z-doublebox,
.lookup-editor-box > table[width="100%"] > tbody > tr > td:first-child > input.z-textbox,
.location-editor-box > table[width="100%"] > tbody > tr > td:first-child > input.z-textbox {
    display: block !important;
    width: 100% !important;
    min-width: 0 !important;
    max-width: none !important;
    height: 32px !important;
    line-height: 20px !important;
    margin: 0 !important;
    box-sizing: border-box !important;
    border-radius: 10px 0 0 10px !important;
    border-right: none !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    padding: 0 !important;
    margin: 0 !important;
    vertical-align: top !important;
    background: transparent !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button {
    display: block !important;
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    margin: 0 !important;
    padding: 0 !important;
    overflow: hidden !important;
    background: transparent !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button > table.number-editor-button.z-button,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button > table.lookup-editor-button.z-button,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button > table.location-editor-button.z-button {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    table-layout: fixed !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
    margin: 0 !important;
    padding: 0 !important;
    overflow: hidden !important;
    background: transparent !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button > table.number-editor-button.z-button > tbody > tr:first-child,
.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button > table.number-editor-button.z-button > tbody > tr:last-child,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button > table.lookup-editor-button.z-button > tbody > tr:first-child,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button > table.lookup-editor-button.z-button > tbody > tr:last-child,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button > table.location-editor-button.z-button > tbody > tr:first-child,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button > table.location-editor-button.z-button > tbody > tr:last-child,
.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button > table.number-editor-button.z-button .z-button-cl,
.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button > table.number-editor-button.z-button .z-button-cr,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button > table.lookup-editor-button.z-button .z-button-cl,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button > table.lookup-editor-button.z-button .z-button-cr,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button > table.location-editor-button.z-button .z-button-cl,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button > table.location-editor-button.z-button .z-button-cr {
    display: none !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button > table.number-editor-button.z-button .z-button-cm,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button > table.lookup-editor-button.z-button .z-button-cm,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button > table.location-editor-button.z-button .z-button-cm {
    display: block !important;
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    margin: 0 !important;
    padding: 0 !important;
    box-sizing: border-box !important;
    border: 1px solid #d8cfbf !important;
    border-left: none !important;
    border-radius: 0 10px 10px 0 !important;
    background-color: #f4ede4 !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 14px 14px !important;
    box-shadow: none !important;
    overflow: hidden !important;
    text-indent: -9999px !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button > table.number-editor-button.z-button .z-button-cm {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 14 14'%3E%3Crect x='2' y='1.5' width='10' height='11' rx='1.5' fill='none' stroke='%2311181d' stroke-width='1.2'/%3E%3Cpath fill='%2311181d' d='M4 3.3h6v1.4H4zM4 6h1.6v1.6H4zm2.2 0h1.6v1.6H6.2zm2.2 0H10v1.6H8.4zM4 8.2h1.6v1.6H4zm2.2 0h1.6v1.6H6.2zm2.2 0H10v1.6H8.4z'/%3E%3C/svg%3E") !important;
}

.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button > table.lookup-editor-button.z-button .z-button-cm {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 14 14'%3E%3Ccircle cx='6' cy='6' r='3.5' fill='none' stroke='%2311181d' stroke-width='1.4'/%3E%3Cpath d='M8.8 8.8L12 12' stroke='%2311181d' stroke-width='1.6' stroke-linecap='round'/%3E%3C/svg%3E") !important;
}

.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button > table.location-editor-button.z-button .z-button-cm {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 14 14'%3E%3Cpath d='M7 12.2c2.3-2.8 3.5-4.8 3.5-6.3A3.5 3.5 0 1 0 3.5 5.9c0 1.5 1.2 3.5 3.5 6.3Z' fill='none' stroke='%2311181d' stroke-width='1.2'/%3E%3Ccircle cx='7' cy='5.8' r='1.4' fill='%2311181d'/%3E%3C/svg%3E") !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button img,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button img,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button img {
    display: none !important;
}

/* Final exact editor widgets after Java split */
.number-editor-box > table,
.lookup-editor-box > table[width="100%"],
.location-editor-box > table[width="100%"] {
    width: 100% !important;
    table-layout: fixed !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
}

.number-editor-box > table > tbody > tr > td:first-child > input.z-decimalbox,
.number-editor-box > table > tbody > tr > td:first-child > input.z-intbox,
.number-editor-box > table > tbody > tr > td:first-child > input.z-longbox,
.number-editor-box > table > tbody > tr > td:first-child > input.z-doublebox,
.lookup-editor-box > table[width="100%"] > tbody > tr > td:first-child > input.z-textbox,
.location-editor-box > table[width="100%"] > tbody > tr > td:first-child > input.z-textbox {
    display: block !important;
    width: 100% !important;
    min-width: 0 !important;
    max-width: none !important;
    height: 32px !important;
    box-sizing: border-box !important;
    margin: 0 !important;
    border-radius: 10px 0 0 10px !important;
    border-right: none !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    padding: 0 !important;
    margin: 0 !important;
    vertical-align: top !important;
    background: transparent !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button.number-editor-button,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button.lookup-editor-button,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button.location-editor-button {
    display: block !important;
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    padding: 0 !important;
    margin: 0 !important;
    background: transparent !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button.number-editor-button > table.editor-button.z-button,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button.lookup-editor-button > table.editor-button.z-button,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button.location-editor-button > table.editor-button.z-button {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    table-layout: fixed !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
    border-radius: 0 10px 10px 0 !important;
    overflow: hidden !important;
    background: transparent !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button.number-editor-button > table.editor-button.z-button > tbody > tr:first-child,
.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button.number-editor-button > table.editor-button.z-button > tbody > tr:last-child,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button.lookup-editor-button > table.editor-button.z-button > tbody > tr:first-child,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button.lookup-editor-button > table.editor-button.z-button > tbody > tr:last-child,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button.location-editor-button > table.editor-button.z-button > tbody > tr:first-child,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button.location-editor-button > table.editor-button.z-button > tbody > tr:last-child,
.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button.number-editor-button > table.editor-button.z-button .z-button-cl,
.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button.number-editor-button > table.editor-button.z-button .z-button-cr,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button.lookup-editor-button > table.editor-button.z-button .z-button-cl,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button.lookup-editor-button > table.editor-button.z-button .z-button-cr,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button.location-editor-button > table.editor-button.z-button .z-button-cl,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button.location-editor-button > table.editor-button.z-button .z-button-cr {
    display: none !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button.number-editor-button > table.editor-button.z-button .z-button-cm,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button.lookup-editor-button > table.editor-button.z-button .z-button-cm,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button.location-editor-button > table.editor-button.z-button .z-button-cm {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    padding: 0 !important;
    margin: 0 !important;
    border: 1px solid #d8cfbf !important;
    border-left: none !important;
    border-radius: 0 10px 10px 0 !important;
    background-color: #f4ede4 !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 14px 14px !important;
    box-shadow: none !important;
    text-indent: -9999px !important;
    overflow: hidden !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button.number-editor-button > table.editor-button.z-button .z-button-cm {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 14 14'%3E%3Crect x='2' y='1.5' width='10' height='11' rx='1.5' fill='none' stroke='%2311181d' stroke-width='1.2'/%3E%3Cpath fill='%2311181d' d='M4 3.3h6v1.4H4zM4 6h1.6v1.6H4zm2.2 0h1.6v1.6H6.2zm2.2 0H10v1.6H8.4zM4 8.2h1.6v1.6H4zm2.2 0h1.6v1.6H6.2zm2.2 0H10v1.6H8.4z'/%3E%3C/svg%3E") !important;
}

.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button.lookup-editor-button > table.editor-button.z-button .z-button-cm {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 14 14'%3E%3Ccircle cx='6' cy='6' r='3.5' fill='none' stroke='%2311181d' stroke-width='1.4'/%3E%3Cpath d='M8.8 8.8L12 12' stroke='%2311181d' stroke-width='1.6' stroke-linecap='round'/%3E%3C/svg%3E") !important;
}

.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button.location-editor-button > table.editor-button.z-button .z-button-cm {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 14 14'%3E%3Cpath d='M7 12.2c2.3-2.8 3.5-4.8 3.5-6.3A3.5 3.5 0 1 0 3.5 5.9c0 1.5 1.2 3.5 3.5 6.3Z' fill='none' stroke='%2311181d' stroke-width='1.2'/%3E%3Ccircle cx='7' cy='5.8' r='1.4' fill='%2311181d'/%3E%3C/svg%3E") !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell > span.z-button.number-editor-button img,
.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell > span.z-button.lookup-editor-button img,
.location-editor-box > table[width="100%"] > tbody > tr > td.location-editor-cell > span.z-button.location-editor-button img {
    display: none !important;
}

/* Final exact number editor integration */
.number-editor-box > table {
    width: 100% !important;
    table-layout: fixed !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
}

.number-editor-box > table > tbody > tr > td:first-child > input.z-decimalbox,
.number-editor-box > table > tbody > tr > td:first-child > input.z-intbox,
.number-editor-box > table > tbody > tr > td:first-child > input.z-longbox,
.number-editor-box > table > tbody > tr > td:first-child > input.z-doublebox {
    display: block !important;
    width: 100% !important;
    min-width: 0 !important;
    max-width: none !important;
    height: 32px !important;
    box-sizing: border-box !important;
    border-radius: 10px 0 0 10px !important;
    border-right: none !important;
}

.number-editor-box > table > tbody > tr > td.number-editor-cell {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    padding: 0 !important;
    background: transparent !important;
}

.number-editor-box table.editor-button.z-button.number-editor-button {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
    border-radius: 0 10px 10px 0 !important;
    overflow: hidden !important;
    background: transparent !important;
}

.number-editor-box table.editor-button.z-button.number-editor-button > tbody > tr:first-child,
.number-editor-box table.editor-button.z-button.number-editor-button > tbody > tr:last-child,
.number-editor-box table.editor-button.z-button.number-editor-button .z-button-cl,
.number-editor-box table.editor-button.z-button.number-editor-button .z-button-cr {
    display: none !important;
}

.number-editor-box table.editor-button.z-button.number-editor-button .z-button-cm {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    padding: 0 !important;
    margin: 0 !important;
    border: 1px solid #d8cfbf !important;
    border-left: none !important;
    border-radius: 0 10px 10px 0 !important;
    background-color: #f4ede4 !important;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 14 14'%3E%3Crect x='2' y='1.5' width='10' height='11' rx='1.5' fill='none' stroke='%2311181d' stroke-width='1.2'/%3E%3Cpath fill='%2311181d' d='M4 3.3h6v1.4H4zM4 6h1.6v1.6H4zm2.2 0h1.6v1.6H6.2zm2.2 0H10v1.6H8.4zM4 8.2h1.6v1.6H4zm2.2 0h1.6v1.6H6.2zm2.2 0H10v1.6H8.4z'/%3E%3C/svg%3E") !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 14px 14px !important;
    box-shadow: none !important;
    text-indent: -9999px !important;
    overflow: hidden !important;
}

/* Final exact lookup editor integration */
.lookup-editor-box > table[width="100%"] {
    width: 100% !important;
    table-layout: fixed !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
}

.lookup-editor-box > table[width="100%"] > tbody > tr > td:first-child > input.z-textbox {
    display: block !important;
    width: 100% !important;
    min-width: 0 !important;
    max-width: none !important;
    height: 32px !important;
    box-sizing: border-box !important;
    border-radius: 10px 0 0 10px !important;
    border-right: none !important;
}

.lookup-editor-box > table[width="100%"] > tbody > tr > td.lookup-editor-cell {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    padding: 0 !important;
    background: transparent !important;
}

.lookup-editor-box table.editor-button.z-button.lookup-editor-button {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
    border-radius: 0 10px 10px 0 !important;
    overflow: hidden !important;
    background: transparent !important;
}

.lookup-editor-box table.editor-button.z-button.lookup-editor-button > tbody > tr:first-child,
.lookup-editor-box table.editor-button.z-button.lookup-editor-button > tbody > tr:last-child,
.lookup-editor-box table.editor-button.z-button.lookup-editor-button .z-button-cl,
.lookup-editor-box table.editor-button.z-button.lookup-editor-button .z-button-cr {
    display: none !important;
}

.lookup-editor-box table.editor-button.z-button.lookup-editor-button .z-button-cm {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    padding: 0 !important;
    margin: 0 !important;
    border: 1px solid #d8cfbf !important;
    border-left: none !important;
    border-radius: 0 10px 10px 0 !important;
    background-color: #f4ede4 !important;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 14 14'%3E%3Ccircle cx='6' cy='6' r='3.5' fill='none' stroke='%2311181d' stroke-width='1.4'/%3E%3Cpath d='M8.8 8.8L12 12' stroke='%2311181d' stroke-width='1.6' stroke-linecap='round'/%3E%3C/svg%3E") !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 14px 14px !important;
    box-shadow: none !important;
    text-indent: -9999px !important;
    overflow: hidden !important;
}

/* Final exact intbox/decimalbox button integration */
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
    border-radius: 0 10px 10px 0 !important;
    overflow: hidden !important;
    background: transparent !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) > tbody > tr:first-child,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) > tbody > tr:last-child,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-cl,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-cr {
    display: none !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    padding: 0 !important;
    margin: 0 !important;
    background: transparent !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-cm {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    padding: 7 !important;
    margin: 0 !important;
    border: 1px solid #d8cfbf !important;
    border-left: none !important;
    border-radius: 0 10px 10px 0 !important;
    background-color: #f4ede4 !important;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 14 14'%3E%3Crect x='2' y='1.5' width='10' height='11' rx='1.5' fill='none' stroke='%2311181d' stroke-width='1.2'/%3E%3Cpath fill='%2311181d' d='M4 3.3h6v1.4H4zM4 6h1.6v1.6H4zm2.2 0h1.6v1.6H6.2zm2.2 0H10v1.6H8.4zM4 8.2h1.6v1.6H4zm2.2 0h1.6v1.6H6.2zm2.2 0H10v1.6H8.4z'/%3E%3C/svg%3E") !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 14px 14px !important;
    box-shadow: none !important;
    text-indent: -9999px !important;
    overflow: hidden !important;
}

/* Final exact numeric field integration */
div[id^="Field_"][style*="white-space:nowrap"] > table {
    width: 100% !important;
    table-layout: fixed !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td:first-child > input.z-decimalbox,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td:first-child > input.z-intbox,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td:first-child > input.z-longbox,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td:first-child > input.z-doublebox {
    display: block !important;
    width: 100% !important;
    min-width: 0 !important;
    max-width: none !important;
    height: 32px !important;
    box-sizing: border-box !important;
    border-radius: 10px 0 0 10px !important;
    border-right: none !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    padding: 0 !important;
    vertical-align: top !important;
    background: transparent !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
    border-radius: 0 10px 10px 0 !important;
    overflow: hidden !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button > tbody > tr:first-child,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button > tbody > tr:last-child,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button .z-button-cl,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button .z-button-cr {
    display: none !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button .z-button-cm {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    padding: 0 !important;
    text-align: center !important;
    vertical-align: middle !important;
    border: 1px solid #d8cfbf !important;
    border-left: none !important;
    border-radius: 0 10px 10px 0 !important;
    background-color: #f4ede4 !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 14px 14px !important;
    box-shadow: none !important;
    overflow: hidden !important;
    text-indent: -9999px !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-cm {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 14 14'%3E%3Crect x='2' y='1.5' width='10' height='11' rx='1.5' fill='none' stroke='%2311181d' stroke-width='1.2'/%3E%3Cpath fill='%2311181d' d='M4 3.3h6v1.4H4zM4 6h1.6v1.6H4zm2.2 0h1.6v1.6H6.2zm2.2 0H10v1.6H8.4zM4 8.2h1.6v1.6H4zm2.2 0h1.6v1.6H6.2zm2.2 0H10v1.6H8.4z'/%3E%3C/svg%3E") !important;
}

/* Final exact lookup field integration */
div[id^="Field_"][style*="background-color: transparent"] > table[width="100%"] {
    width: 100% !important;
    table-layout: fixed !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
}

div[id^="Field_"][style*="background-color: transparent"] > table[width="100%"] > tbody > tr > td:first-child > input.z-textbox {
    display: block !important;
    width: 100% !important;
    min-width: 0 !important;
    max-width: none !important;
    height: 32px !important;
    box-sizing: border-box !important;
    border-radius: 10px 0 0 10px !important;
    border-right: none !important;
}

div[id^="Field_"][style*="background-color: transparent"] > table[width="100%"] > tbody > tr > td.editor-button {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    padding: 0 !important;
    vertical-align: top !important;
    background: transparent !important;
}

div[id^="Field_"][style*="background-color: transparent"] > table[width="100%"] > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="PickOpen10.png"]),
div[id^="Field_"][style*="background-color: transparent"] > table[width="100%"] > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="BPartner10.png"]) {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
    border-radius: 0 10px 10px 0 !important;
    overflow: hidden !important;
}

div[id^="Field_"][style*="background-color: transparent"] > table[width="100%"] > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="PickOpen10.png"]) > tbody > tr:first-child,
div[id^="Field_"][style*="background-color: transparent"] > table[width="100%"] > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="PickOpen10.png"]) > tbody > tr:last-child,
div[id^="Field_"][style*="background-color: transparent"] > table[width="100%"] > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="BPartner10.png"]) > tbody > tr:first-child,
div[id^="Field_"][style*="background-color: transparent"] > table[width="100%"] > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="BPartner10.png"]) > tbody > tr:last-child,
div[id^="Field_"][style*="background-color: transparent"] > table[width="100%"] > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="PickOpen10.png"]) .z-button-cl,
div[id^="Field_"][style*="background-color: transparent"] > table[width="100%"] > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="PickOpen10.png"]) .z-button-cr,
div[id^="Field_"][style*="background-color: transparent"] > table[width="100%"] > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="BPartner10.png"]) .z-button-cl,
div[id^="Field_"][style*="background-color: transparent"] > table[width="100%"] > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="BPartner10.png"]) .z-button-cr {
    display: none !important;
}

div[id^="Field_"][style*="background-color: transparent"] > table[width="100%"] > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="PickOpen10.png"]) .z-button-cm,
div[id^="Field_"][style*="background-color: transparent"] > table[width="100%"] > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="BPartner10.png"]) .z-button-cm {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    padding: 0 !important;
    text-align: center !important;
    vertical-align: middle !important;
    border: 1px solid #d8cfbf !important;
    border-left: none !important;
    border-radius: 0 10px 10px 0 !important;
    background-color: #f4ede4 !important;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 14 14'%3E%3Ccircle cx='6' cy='6' r='3.5' fill='none' stroke='%2311181d' stroke-width='1.4'/%3E%3Cpath d='M8.8 8.8L12 12' stroke='%2311181d' stroke-width='1.6' stroke-linecap='round'/%3E%3C/svg%3E") !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 14px 14px !important;
    box-shadow: none !important;
    overflow: hidden !important;
    text-indent: -9999px !important;
}

/* Final inner-toolbar hover artifact fix */
.toolbar-button,
.toolbar-button-large,
.embedded-toolbar-button,
.toolbar-button img,
.toolbar-button-large img,
.embedded-toolbar-button img {
    outline: none !important;
}

.toolbar-button img:hover,
.toolbar-button-large img:hover,
.embedded-toolbar-button img:hover,
.toolbar-button:hover img,
.toolbar-button-large:hover img,
.embedded-toolbar-button:hover img,
.toolbar-button:focus img,
.toolbar-button-large:focus img,
.embedded-toolbar-button:focus img {
    box-shadow: none !important;
    outline: none !important;
}

/* Final confirmation modal hidden-button guard */
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td[style*="display:none"],
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td[style*="display: none"],
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td[style*="display:none"] > span,
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td[style*="display: none"] > span,
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td[style*="display:none"] > span > table,
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td[style*="display: none"] > span > table,
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td.z-hbox-sep[style*="display:none"],
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td.z-hbox-sep[style*="display: none"],
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td[style*="display:none"] *,
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td[style*="display: none"] * {
    display: none !important;
    visibility: hidden !important;
    width: 0 !important;
    min-width: 0 !important;
    max-width: 0 !important;
    padding: 0 !important;
    margin: 0 !important;
    border: 0 !important;
    overflow: hidden !important;
}

/* Final header right padding fix */
.app-userpanel {
    box-sizing: border-box !important;
    width: calc(100% - 28px) !important;
    padding-right: 28px !important;
}

.app-userpanel > tbody > tr > td[align="right"] {
    padding-right: 0 !important;
}

/* Final process action buttons */
table.action-button.z-button,
table.action-text-button.z-button {
    border-collapse: collapse !important;
    border-spacing: 0 !important;
    border-radius: 12px !important;
    overflow: hidden !important;
    margin: 0 !important;
}

table.action-button.z-button > tbody > tr:first-child,
table.action-button.z-button > tbody > tr:last-child,
table.action-text-button.z-button > tbody > tr:first-child,
table.action-text-button.z-button > tbody > tr:last-child {
    display: none !important;
}

table.action-button.z-button .z-button-cl,
table.action-button.z-button .z-button-cr,
table.action-text-button.z-button .z-button-cl,
table.action-text-button.z-button .z-button-cr {
    display: none !important;
}

table.action-button.z-button .z-button-cm,
table.action-text-button.z-button .z-button-cm {
    display: block !important;
    min-width: 124px !important;
    height: 40px !important;
    line-height: 40px !important;
    padding: 0 18px !important;
    text-align: center !important;
    border-radius: 12px !important;
    background: #1b2329 !important;
    background-image: none !important;
    color: #f8fbfc !important;
    font-family: var(--ly-font-sans) !important;
    font-weight: 700 !important;
    box-shadow: none !important;
    white-space: nowrap !important;
}

table.action-button.z-button .z-button-cm img,
table.action-text-button.z-button .z-button-cm img {
    vertical-align: middle !important;
    margin-right: 6px !important;
}

table.action-button.z-button:has(img[src*="Ok16.png"]) .z-button-cm,
table.action-text-button.z-button:has(img[src*="Ok16.png"]) .z-button-cm {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    color: #11181d !important;
}

/* Final lookup editor fix */
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td:first-child > input.z-textbox {
    display: block !important;
    width: 100% !important;
    min-width: 0 !important;
    max-width: none !important;
    height: 32px !important;
    line-height: 20px !important;
    box-sizing: border-box !important;
    border-radius: 10px 0 0 10px !important;
    border-right: none !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img:not([src*="Calculator10.png"])) {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    table-layout: fixed !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
    overflow: hidden !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img:not([src*="Calculator10.png"])) .z-button-tl,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img:not([src*="Calculator10.png"])) .z-button-tm,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img:not([src*="Calculator10.png"])) .z-button-tr,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img:not([src*="Calculator10.png"])) .z-button-cl,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img:not([src*="Calculator10.png"])) .z-button-cm,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img:not([src*="Calculator10.png"])) .z-button-cr,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img:not([src*="Calculator10.png"])) .z-button-bl,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img:not([src*="Calculator10.png"])) .z-button-bm,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img:not([src*="Calculator10.png"])) .z-button-br {
    background-image: none !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img:not([src*="Calculator10.png"])) .z-button-cm {
    width: 32px !important;
    height: 32px !important;
    padding: 0 !important;
    text-align: center !important;
    vertical-align: middle !important;
    border: 1px solid #d8cfbf !important;
    border-left: none !important;
    border-radius: 0 10px 10px 0 !important;
    background-color: #f4ede4 !important;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 14 14'%3E%3Ccircle cx='6' cy='6' r='3.5' fill='none' stroke='%2311181d' stroke-width='1.4'/%3E%3Cpath d='M8.8 8.8L12 12' stroke='%2311181d' stroke-width='1.6' stroke-linecap='round'/%3E%3C/svg%3E") !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 14px 14px !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img:not([src*="Calculator10.png"])) .z-button-cm img {
    display: none !important;
}

/* Final calculator editor cleanup */
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) {
    border-collapse: collapse !important;
    border-spacing: 0 !important;
    border-radius: 0 10px 10px 0 !important;
    overflow: hidden !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) > tbody > tr:first-child,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) > tbody > tr:last-child {
    display: none !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-cl,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-cr {
    display: none !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-cm {
    border: 1px solid #d8cfbf !important;
    border-left: none !important;
    border-radius: 0 10px 10px 0 !important;
    background: #f4ede4 !important;
    background-image: none !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-tl,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-tm,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-tr,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-cl,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-cr,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-bl,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-bm,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button:has(img[src*="Calculator10.png"]) .z-button-br {
    background: #f4ede4 !important;
    background-image: none !important;
    border: none !important;
}

/* Final datebox fix in form rows */
.z-row-cnt > span.z-datebox {
    display: flex !important;
    align-items: stretch !important;
    width: 100% !important;
    max-width: 100% !important;
    box-sizing: border-box !important;
}

.z-row-cnt > span.z-datebox > input.z-datebox-inp,
.z-row-cnt > span.z-datebox > input.z-datebox-inp[style] {
    flex: 1 1 auto !important;
    width: auto !important;
    min-width: 0 !important;
    max-width: none !important;
    height: 32px !important;
    box-sizing: border-box !important;
    border-radius: 10px 0 0 10px !important;
    border-right: none !important;
}

.z-row-cnt > span.z-datebox > span.z-datebox-btn,
.z-row-cnt > span.z-datebox > span.z-datebox-btn[style] {
    position: relative !important;
    top: auto !important;
    left: auto !important;
    display: inline-flex !important;
    align-items: center !important;
    justify-content: center !important;
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    margin: 0 !important;
    padding: 0 !important;
    box-sizing: border-box !important;
    border: 1px solid #d8cfbf !important;
    border-left: none !important;
    border-radius: 0 10px 10px 0 !important;
    background-color: #f4ede4 !important;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 14 14'%3E%3Crect x='2' y='3' width='10' height='9' rx='1.5' fill='none' stroke='%2311181d' stroke-width='1.2'/%3E%3Cpath fill='%2311181d' d='M4 1.5h1.2v2H4zm4.8 0H10v2H8.8zM3 5h8v1.2H3z'/%3E%3C/svg%3E") !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 14px 14px !important;
    overflow: hidden !important;
}

.z-row-cnt > span.z-datebox > span.z-datebox-btn > img.z-datebox-img,
.z-row-cnt > span.z-datebox > span.z-datebox-btn > img.z-datebox-img[style] {
    display: block !important;
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    margin: 0 !important;
    padding: 0 !important;
    opacity: 0 !important;
}

/* Final datebox popup styling */
.z-datebox-pp {
    background: #f7f1e5 !important;
    border: 1px solid #173043 !important;
    box-shadow: 0 12px 28px rgba(15, 21, 26, 0.18) !important;
    padding: 6px !important;
    font-family: var(--ly-font-sans) !important;
}

.z-datebox-pp table {
    border-collapse: collapse !important;
}

.z-datebox-calyear,
.z-datebox-calmon,
.z-datebox-calday {
    background: transparent !important;
    color: #1b2329 !important;
    font-family: var(--ly-font-sans) !important;
}

.z-datebox-calyear td {
    background: #f1e2bf !important;
    color: #1b2329 !important;
    font-weight: 700 !important;
    border: none !important;
    padding: 6px 8px !important;
}

.z-datebox-calyear img[id$="!ly"],
.z-datebox-calyear img[id$="!ry"] {
    width: 14px !important;
    height: 14px !important;
    opacity: 0 !important;
}

.z-datebox-calyear td[align="right"],
.z-datebox-calyear td[align="left"] {
    position: relative !important;
    width: 22px !important;
}

.z-datebox-calyear td[align="right"]:before,
.z-datebox-calyear td[align="left"]:before {
    content: "" !important;
    position: absolute !important;
    inset: 0 !important;
    margin: auto !important;
    width: 10px !important;
    height: 10px !important;
    border-top: 2px solid #1b2329 !important;
    border-right: 2px solid #1b2329 !important;
}

.z-datebox-calyear td[align="right"]:before {
    transform: rotate(-135deg) !important;
}

.z-datebox-calyear td[align="left"]:before {
    transform: rotate(45deg) !important;
}

.z-datebox-calmon td,
.z-datebox-calday td {
    border: 1px solid rgba(23, 48, 67, 0.12) !important;
    padding: 6px 8px !important;
    text-align: center !important;
    color: #1b2329 !important;
    background: #fffdfa !important;
}

.z-datebox-caldow td {
    background: #f4ede4 !important;
    color: #1b2329 !important;
    font-weight: 700 !important;
}

.z-datebox-calmon td.z-datebox-seld,
.z-datebox-calday td.z-datebox-seld {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    color: #11181d !important;
    font-weight: 700 !important;
}

/* Final datebox fix for process parameter ranges */
.z-row-cnt table.z-hbox > tbody > tr > td > span.z-datebox {
    display: inline-flex !important;
    align-items: stretch !important;
    width: 142px !important;
    max-width: 142px !important;
    box-sizing: border-box !important;
    vertical-align: top !important;
}

.z-row-cnt table.z-hbox > tbody > tr > td > span.z-datebox > input.z-datebox-inp,
.z-row-cnt table.z-hbox > tbody > tr > td > span.z-datebox > input.z-datebox-inp[style] {
    flex: 1 1 auto !important;
    width: auto !important;
    min-width: 0 !important;
    max-width: none !important;
    height: 32px !important;
    box-sizing: border-box !important;
    border-radius: 10px 0 0 10px !important;
    border-right: none !important;
}

.z-row-cnt table.z-hbox > tbody > tr > td > span.z-datebox > span.z-datebox-btn,
.z-row-cnt table.z-hbox > tbody > tr > td > span.z-datebox > span.z-datebox-btn[style] {
    position: relative !important;
    top: auto !important;
    left: auto !important;
    display: inline-flex !important;
    align-items: center !important;
    justify-content: center !important;
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    margin: 0 !important;
    padding: 0 !important;
    box-sizing: border-box !important;
    border: 1px solid #d8cfbf !important;
    border-left: none !important;
    border-radius: 0 10px 10px 0 !important;
    background-color: #f4ede4 !important;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 14 14'%3E%3Crect x='2' y='3' width='10' height='9' rx='1.5' fill='none' stroke='%2311181d' stroke-width='1.2'/%3E%3Cpath fill='%2311181d' d='M4 1.5h1.2v2H4zm4.8 0H10v2H8.8zM3 5h8v1.2H3z'/%3E%3C/svg%3E") !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 14px 14px !important;
    overflow: hidden !important;
}

.z-row-cnt table.z-hbox > tbody > tr > td > span.z-datebox > span.z-datebox-btn > img.z-datebox-img,
.z-row-cnt table.z-hbox > tbody > tr > td > span.z-datebox > span.z-datebox-btn > img.z-datebox-img[style] {
    display: block !important;
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    margin: 0 !important;
    padding: 0 !important;
    opacity: 0 !important;
}

/* Final combobox right-corner cleanup */
.z-combobox table td:last-child {
    width: 32px !important;
    padding: 0 !important;
    background: transparent !important;
    border: none !important;
}

.z-combobox .z-combobox-btn {
    border-radius: 0 10px 10px 0 !important;
    overflow: hidden !important;
    border-left: none !important;
}

.z-combobox .z-combobox-img {
    border-radius: 0 10px 10px 0 !important;
    overflow: hidden !important;
    box-shadow: none !important;
}

/* Final modal hidden-button guard */
.z-window-modal table.z-hbox[style*="height:52px"] td[style*="display:none"],
.z-window-modal table.z-hbox[style*="height:52px"] td[style*="display:none"] > span,
.z-window-modal table.z-hbox[style*="height:52px"] td[style*="display:none"] > span > table,
.z-window-modal table.z-hbox[style*="height:52px"] td.z-hbox-sep[style*="display:none"] {
    display: none !important;
}

/* Final confirmation modal footer fix */
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr {
    display: flex !important;
    align-items: center !important;
    justify-content: space-between !important;
    width: 100% !important;
    gap: 12px !important;
}

.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td {
    display: block !important;
}

.z-window-modal table.action-text-button > tbody > tr:first-child,
.z-window-modal table.action-text-button > tbody > tr:last-child {
    display: none !important;
}

.z-window-modal table.action-text-button .z-button-cl,
.z-window-modal table.action-text-button .z-button-cr {
    display: none !important;
}

.z-window-modal table.action-text-button .z-button-cm {
    display: block !important;
    min-width: 126px !important;
    padding: 0 18px !important;
    line-height: 40px !important;
    text-align: center !important;
    border-radius: 12px !important;
    box-shadow: none !important;
}

.z-window-modal table.action-text-button .z-button-cm img {
    vertical-align: middle !important;
    margin-right: 6px !important;
}

/* Final confirmation modal overrides */
.z-window-modal,
.z-window-modal-tl,
.z-window-modal-tr,
.z-window-modal-hl,
.z-window-modal-hr,
.z-window-modal-hm,
.z-window-modal-cl,
.z-window-modal-cr,
.z-window-modal-cm,
.z-window-modal-bl,
.z-window-modal-br {
    background: #162027 !important;
    background-image: none !important;
}

.z-window-modal {
    border-radius: 14px !important;
    overflow: hidden !important;
}

.z-window-modal-header {
    background: #162027 !important;
    color: #f8fbfc !important;
    border: none !important;
    box-shadow: none !important;
}

.z-window-modal-cnt,
.z-window-modal-cnt[style] {
    background: #f7f1e5 !important;
    border-radius: 0 0 14px 14px !important;
    box-shadow: none !important;
}

.z-window-modal .z-window-modal-close {
    background: #162027 !important;
    border-radius: 0 !important;
}

.z-window-modal .z-window-modal-close:before {
    content: "×" !important;
    color: #f8fbfc !important;
    font-size: 20px !important;
    line-height: 28px !important;
}

.z-window-modal .z-window-modal-close {
    color: transparent !important;
    font-size: 0 !important;
}

.z-window-modal .z-separator-hor-bar {
    height: 1px !important;
    background: rgba(209, 138, 20, 0.28) !important;
    border: none !important;
}

.z-window-modal div[style*="max-height: 350pt"] {
    color: #14191d !important;
    font-family: var(--ly-font-sans) !important;
}

.z-window-modal table.action-text-button {
    min-width: 126px !important;
    border-collapse: separate !important;
    border-spacing: 0 !important;
    border-radius: 12px !important;
    overflow: hidden !important;
}

.z-window-modal table.action-text-button .z-button-tl,
.z-window-modal table.action-text-button .z-button-tm,
.z-window-modal table.action-text-button .z-button-tr,
.z-window-modal table.action-text-button .z-button-cl,
.z-window-modal table.action-text-button .z-button-cm,
.z-window-modal table.action-text-button .z-button-cr,
.z-window-modal table.action-text-button .z-button-bl,
.z-window-modal table.action-text-button .z-button-bm,
.z-window-modal table.action-text-button .z-button-br {
    background: #1b2329 !important;
    background-image: none !important;
    color: #f8fbfc !important;
    font-family: var(--ly-font-sans) !important;
    font-weight: 700 !important;
    box-shadow: none !important;
}

.z-window-modal table.action-text-button .z-button-cm {
    line-height: 40px !important;
    text-align: center !important;
    vertical-align: middle !important;
    padding: 0 18px !important;
}

.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-tl,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-tm,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-tr,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-cl,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-cm,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-cr,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-bl,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-bm,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-br {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    color: #11181d !important;
}

.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-tl,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-tm,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-tr,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-cl,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-cm,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-cr,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-bl,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-bm,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-br {
    background: #1b2329 !important;
    color: #f8fbfc !important;
}

/* Final confirmation modal footer layout */
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr {
    display: flex !important;
    justify-content: flex-end !important;
    align-items: center !important;
    gap: 10px !important;
}

.z-window-modal table.z-hbox[style*="height:52px"] td.z-hbox-sep {
    display: none !important;
    width: 0 !important;
}

.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td[style*="display:none;"] {
    display: none !important;
}

.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td:has(table.action-text-button img[src*="Cancel16.png"]) > span.z-button,
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td:has(table.action-text-button img[src*="Cancel16.png"]) > span.z-button > table.action-text-button {
    display: table !important;
}

.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td:has(table.action-text-button img[src*="Ok16.png"]) > span.z-button,
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td:has(table.action-text-button img[src*="Ok16.png"]) > span.z-button > table.action-text-button {
    display: table !important;
}

.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td:has(table.action-text-button img[src*="Cancel16.png"]) .z-button-cm,
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td:has(table.action-text-button img[src*="Ok16.png"]) .z-button-cm {
    min-width: 126px !important;
}

.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td:has(table.action-text-button .z-button-cm) table.action-text-button {
    margin: 0 !important;
}

.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td:nth-child(5),
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td:nth-child(7),
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td:nth-child(9),
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td:nth-child(11),
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td:nth-child(13) {
    display: none !important;
}

.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td:nth-child(1),
.z-window-modal table.z-hbox[style*="height:52px"] > tbody > tr > td:nth-child(3) {
    white-space: nowrap !important;
}

/* Confirmation modal styling */
.z-window-modal {
    border-radius: 14px !important;
    overflow: hidden !important;
    box-shadow: 0 24px 48px rgba(15, 21, 26, 0.26) !important;
}

.z-window-modal-tl,
.z-window-modal-tr,
.z-window-modal-hl,
.z-window-modal-hr,
.z-window-modal-cl,
.z-window-modal-cr,
.z-window-modal-bl,
.z-window-modal-br {
    background-image: none !important;
}

.z-window-modal-header {
    background: #162027 !important;
    color: #f8fbfc !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 15px !important;
    font-weight: 700 !important;
    line-height: 20px !important;
    padding: 10px 40px 10px 14px !important;
    border-bottom: 1px solid rgba(239, 176, 38, 0.22) !important;
}

.z-window-modal-cnt {
    background: #f7f1e5 !important;
    color: #14191d !important;
    font-family: var(--ly-font-sans) !important;
}

.z-window-modal .z-separator-hor-bar {
    height: 1px !important;
    background: rgba(215, 139, 14, 0.28) !important;
    border: none !important;
}

.z-window-modal .z-window-modal-close {
    width: 28px !important;
    height: 28px !important;
    top: 6px !important;
    right: 8px !important;
    background: #162027 !important;
    border-radius: 8px !important;
    box-shadow: none !important;
}

.z-window-modal .z-window-modal-close:before {
    content: "×" !important;
    display: block !important;
    color: #f8fbfc !important;
    font-size: 18px !important;
    font-weight: 700 !important;
    line-height: 28px !important;
    text-align: center !important;
}

.z-window-modal .z-window-modal-close {
    color: transparent !important;
    font-size: 0 !important;
}

.z-window-modal .z-hbox,
.z-window-modal .z-hbox td,
.z-window-modal .z-label,
.z-window-modal span,
.z-window-modal div {
    font-family: var(--ly-font-sans) !important;
}

.z-window-modal .action-text-button {
    min-width: 126px !important;
    border-radius: 12px !important;
    overflow: hidden !important;
}

.z-window-modal .action-text-button .z-button-tl,
.z-window-modal .action-text-button .z-button-tm,
.z-window-modal .action-text-button .z-button-tr,
.z-window-modal .action-text-button .z-button-cl,
.z-window-modal .action-text-button .z-button-cm,
.z-window-modal .action-text-button .z-button-cr,
.z-window-modal .action-text-button .z-button-bl,
.z-window-modal .action-text-button .z-button-bm,
.z-window-modal .action-text-button .z-button-br {
    background: #1b2329 !important;
    background-image: none !important;
    color: #f8fbfc !important;
    font-family: var(--ly-font-sans) !important;
    font-weight: 700 !important;
}

.z-window-modal .action-text-button .z-button-cm {
    text-align: center !important;
    vertical-align: middle !important;
    line-height: 40px !important;
    padding: 0 18px !important;
    white-space: nowrap !important;
}

.z-window-modal .action-text-button .z-button-cm img {
    vertical-align: middle !important;
    margin-right: 6px !important;
}

.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-tl,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-tm,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-tr,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-cl,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-cm,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-cr,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-bl,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-bm,
.z-window-modal table.action-text-button:has(img[src*="Ok16.png"]) .z-button-br {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    color: #11181d !important;
}

.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-tl,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-tm,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-tr,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-cl,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-cm,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-cr,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-bl,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-bm,
.z-window-modal table.action-text-button:has(img[src*="Cancel16.png"]) .z-button-br {
    background: #1b2329 !important;
    color: #f8fbfc !important;
}

/* Final exact NumberBox fix: field fills cell, calculator square and visible */
div[id^="Field_"][style*="white-space:nowrap"] {
    display: block !important;
    width: 100% !important;
    white-space: nowrap !important;
    box-sizing: border-box !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table {
    width: 100% !important;
    table-layout: fixed !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td:first-child {
    width: auto !important;
    padding: 0 !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td:first-child > input.z-decimalbox,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td:first-child > input.z-intbox,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td:first-child > input.z-longbox,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td:first-child > input.z-doublebox {
    display: block !important;
    width: 100% !important;
    min-width: 0 !important;
    max-width: none !important;
    height: 32px !important;
    line-height: 20px !important;
    box-sizing: border-box !important;
    border-radius: 10px 0 0 10px !important;
    border-right: none !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    padding: 0 !important;
    vertical-align: top !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button {
    display: block !important;
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button > span.z-button > table.editor-button.z-button {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
    max-height: 32px !important;
    table-layout: fixed !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
    overflow: hidden !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button .z-button-tl,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button .z-button-tm,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button .z-button-tr,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button .z-button-cl,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button .z-button-cm,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button .z-button-cr,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button .z-button-bl,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button .z-button-bm,
div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button .z-button-br {
    background-image: none !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button .z-button-cm {
    width: 32px !important;
    height: 32px !important;
    padding: 0 !important;
    text-align: center !important;
    vertical-align: middle !important;
    background: linear-gradient(180deg, #f3b31f 0%, #de9800 100%) !important;
}

div[id^="Field_"][style*="white-space:nowrap"] > table > tbody > tr > td.editor-button .z-button-cm img[src*="Calculator10.png"] {
    display: block !important;
    width: 14px !important;
    height: 14px !important;
    margin: 0 auto !important;
    opacity: 1 !important;
    visibility: visible !important;
}

/* Calculator popup */
.z-popup {
    border-radius: 12px !important;
    overflow: hidden !important;
    box-shadow: 0 18px 36px rgba(15, 21, 26, 0.22) !important;
}

.z-popup-tl,
.z-popup-tr,
.z-popup-cl,
.z-popup-cr,
.z-popup-cm,
.z-popup-bl,
.z-popup-br,
.z-popup-bm {
    background-image: none !important;
}

.z-popup-cl,
.z-popup-cr,
.z-popup-cm {
    background: #f7f1e5 !important;
}

.z-popup-cnt {
    background: #f7f1e5 !important;
    padding: 6px !important;
    font-family: var(--ly-font-sans) !important;
}

.z-popup-cnt > .z-vbox {
    width: auto !important;
}

.z-popup-cnt input.z-textbox {
    width: 100% !important;
    min-height: 34px !important;
    box-sizing: border-box !important;
    border: 1px solid rgba(214, 171, 92, 0.45) !important;
    border-radius: 10px !important;
    background: #fffdfa !important;
    color: #1b2329 !important;
    font-family: var(--ly-font-sans) !important;
    font-size: 14px !important;
    line-height: 20px !important;
    padding: 6px 10px !important;
}

.z-popup-cnt .z-vbox-sep td {
    height: 2px !important;
}

.z-popup-cnt .z-hbox {
    border-collapse: separate !important;
    border-spacing: 2px !important;
}

.z-popup-cnt .z-hbox-sep {
    width: 2px !important;
}

.z-popup-cnt .z-hbox .z-button {
    display: inline-block !important;
}

.z-popup-cnt .z-hbox table.z-button {
    width: 30px !important;
    min-width: 30px !important;
    max-width: 30px !important;
    height: 30px !important;
    min-height: 30px !important;
    max-height: 30px !important;
    border-collapse: collapse !important;
    border-spacing: 0 !important;
    border-radius: 8px !important;
    overflow: visible !important;
    table-layout: fixed !important;
    box-shadow: none !important;
}

.z-popup-cnt .z-hbox table.z-button .z-button-tl,
.z-popup-cnt .z-hbox table.z-button .z-button-tm,
.z-popup-cnt .z-hbox table.z-button .z-button-tr,
.z-popup-cnt .z-hbox table.z-button .z-button-cl,
.z-popup-cnt .z-hbox table.z-button .z-button-cm,
.z-popup-cnt .z-hbox table.z-button .z-button-cr,
.z-popup-cnt .z-hbox table.z-button .z-button-bl,
.z-popup-cnt .z-hbox table.z-button .z-button-bm,
.z-popup-cnt .z-hbox table.z-button .z-button-br {
    background: #1b2329 !important;
    background-image: none !important;
    color: #f8fbfc !important;
    font-family: var(--ly-font-sans) !important;
    font-weight: 700 !important;
    border-top: none !important;
    box-shadow: none !important;
}

.z-popup-cnt .z-hbox table.z-button .z-button-tl,
.z-popup-cnt .z-hbox table.z-button .z-button-tm,
.z-popup-cnt .z-hbox table.z-button .z-button-tr,
.z-popup-cnt .z-hbox table.z-button .z-button-cl,
.z-popup-cnt .z-hbox table.z-button .z-button-cr,
.z-popup-cnt .z-hbox table.z-button .z-button-bl,
.z-popup-cnt .z-hbox table.z-button .z-button-bm,
.z-popup-cnt .z-hbox table.z-button .z-button-br {
    width: 0 !important;
    min-width: 0 !important;
    max-width: 0 !important;
    height: 0 !important;
    min-height: 0 !important;
    max-height: 0 !important;
    padding: 0 !important;
    font-size: 0 !important;
    line-height: 0 !important;
}

.z-popup-cnt .z-hbox table.z-button .z-button-cm {
    text-align: center !important;
    vertical-align: middle !important;
    width: 30px !important;
    min-width: 30px !important;
    max-width: 30px !important;
    height: 30px !important;
    min-height: 30px !important;
    max-height: 30px !important;
    font-size: 13px !important;
    line-height: 30px !important;
    padding: 0 !important;
    text-indent: 0 !important;
    overflow: visible !important;
    border-radius: 8px !important;
    box-shadow: none !important;
}

.z-popup-cnt .z-hbox table.z-button:hover .z-button-tl,
.z-popup-cnt .z-hbox table.z-button:hover .z-button-tm,
.z-popup-cnt .z-hbox table.z-button:hover .z-button-tr,
.z-popup-cnt .z-hbox table.z-button:hover .z-button-cl,
.z-popup-cnt .z-hbox table.z-button:hover .z-button-cm,
.z-popup-cnt .z-hbox table.z-button:hover .z-button-cr,
.z-popup-cnt .z-hbox table.z-button:hover .z-button-bl,
.z-popup-cnt .z-hbox table.z-button:hover .z-button-bm,
.z-popup-cnt .z-hbox table.z-button:hover .z-button-br {
    background: linear-gradient(180deg, #f3b31f 0%, #de9800 100%) !important;
    color: #11181d !important;
}

.z-popup-cnt .z-hbox table.z-button.z-button-disd .z-button-tl,
.z-popup-cnt .z-hbox table.z-button.z-button-disd .z-button-tm,
.z-popup-cnt .z-hbox table.z-button.z-button-disd .z-button-tr,
.z-popup-cnt .z-hbox table.z-button.z-button-disd .z-button-cl,
.z-popup-cnt .z-hbox table.z-button.z-button-disd .z-button-cm,
.z-popup-cnt .z-hbox table.z-button.z-button-disd .z-button-cr,
.z-popup-cnt .z-hbox table.z-button.z-button-disd .z-button-bl,
.z-popup-cnt .z-hbox table.z-button.z-button-disd .z-button-bm,
.z-popup-cnt .z-hbox table.z-button.z-button-disd .z-button-br {
    background: #8c949b !important;
    color: #eef2f4 !important;
}

/* Final calculator keypad compaction */
.z-popup-cnt > .z-vbox {
    width: auto !important;
}

.z-popup-cnt > .z-vbox > tbody > tr:first-child input.z-textbox {
    width: 148px !important;
    min-width: 148px !important;
    max-width: 148px !important;
}

.z-popup-cnt > .z-vbox .z-vbox-sep td {
    height: 1px !important;
}

.z-popup-cnt > .z-vbox .z-hbox {
    border-spacing: 1px !important;
}

.z-popup-cnt > .z-vbox .z-hbox-sep {
    width: 1px !important;
}

.z-popup,
.z-popup-cl,
.z-popup-cr,
.z-popup-cm,
.z-popup-cnt,
.z-popup-cnt > table.z-vbox,
.z-popup-cnt > table.z-vbox > tbody,
.z-popup-cnt > table.z-vbox > tbody > tr,
.z-popup-cnt > table.z-vbox > tbody > tr > td {
    width: auto !important;
    max-width: none !important;
}

.z-popup-cnt > table.z-vbox {
    display: inline-table !important;
}

.z-popup-cnt > table.z-vbox > tbody > tr > td > table.z-hbox {
    display: inline-table !important;
    width: auto !important;
    max-width: none !important;
    table-layout: auto !important;
}

.z-popup-cnt > .z-vbox .z-hbox table.z-button {
    width: 28px !important;
    min-width: 28px !important;
    max-width: 28px !important;
    height: 28px !important;
    min-height: 28px !important;
    max-height: 28px !important;
    border-radius: 7px !important;
}

.z-popup-cnt > .z-vbox .z-hbox table.z-button tbody > tr:first-child,
.z-popup-cnt > .z-vbox .z-hbox table.z-button tbody > tr:last-child {
    display: none !important;
}

.z-popup-cnt > .z-vbox .z-hbox table.z-button .z-button-cl,
.z-popup-cnt > .z-vbox .z-hbox table.z-button .z-button-cr {
    display: none !important;
}

.z-popup-cnt > .z-vbox .z-hbox table.z-button .z-button-cm {
    width: 28px !important;
    min-width: 28px !important;
    max-width: 28px !important;
    height: 28px !important;
    min-height: 28px !important;
    max-height: 28px !important;
    line-height: 28px !important;
    font-size: 13px !important;
    border-radius: 7px !important;
}

/* Final calculator popup width lock */
.z-popup-cnt > table.z-vbox,
.z-popup-cnt > .z-vbox {
    width: 148px !important;
    min-width: 148px !important;
    max-width: 148px !important;
}

.z-popup-cnt > table.z-vbox > tbody > tr > td,
.z-popup-cnt > .z-vbox > tbody > tr > td {
    width: auto !important;
    padding: 0 !important;
}

.z-popup-cnt > table.z-vbox > tbody > tr > td > table.z-hbox,
.z-popup-cnt > .z-vbox > tbody > tr > td > table.z-hbox {
    width: 148px !important;
    min-width: 148px !important;
    max-width: 148px !important;
    table-layout: fixed !important;
    border-collapse: separate !important;
    border-spacing: 1px !important;
}

.z-popup-cnt > table.z-vbox > tbody > tr:first-child > td > input.z-textbox,
.z-popup-cnt > .z-vbox > tbody > tr:first-child > td > input.z-textbox {
    width: 148px !important;
    min-width: 148px !important;
    max-width: 148px !important;
}

/* Final window side tab selection */
.adwindow-navbtn-sel,
.adwindow-left-navbtn-sel,
.adwindow-right-navbtn-sel {
    background: linear-gradient(180deg, #f2b52f 0%, #d78b0e 100%) !important;
    color: #11181d !important;
    border-color: rgba(209, 138, 20, 0.45) !important;
    box-shadow: inset 0 0 0 1px rgba(209, 138, 20, 0.45) !important;
}

.action-button,
.action-text-button {
    min-width: 104px !important;
    border-radius: 10px !important;
    overflow: hidden !important;
    font-family: var(--ly-font-sans) !important;
}

.action-button .z-button-cm,
.action-text-button .z-button-cm {
    display: table-cell !important;
    text-align: center !important;
    vertical-align: middle !important;
    white-space: nowrap !important;
    line-height: 1.2 !important;
    font-family: var(--ly-font-sans) !important;
    padding-left: 16px !important;
    padding-right: 16px !important;
}

.action-button .z-button-cm img[src*="spacer.gif"],
.action-text-button .z-button-cm img[src*="spacer.gif"] {
    display: none !important;
}

/* Final popup section integration */
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox {
    background: #fffdfa !important;
    border-radius: 16px !important;
    overflow: hidden !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox > .z-tabpanels {
    background: #fffdfa !important;
    border: none !important;
    border-radius: 0 0 16px 16px !important;
    overflow: hidden !important;
    padding: 0 10px 10px !important;
    box-sizing: border-box !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox > .z-tabpanels > .z-tabpanel,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox > .z-tabpanels > .z-tabpanel > .z-tabpanel-cnt,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-window-embedded,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-window-embedded-cnt-noborder,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-border-layout,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-center,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-center-body,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-south,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-south-body,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-grid,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-grid-body {
    background: #fffdfa !important;
    border: none !important;
    border-radius: 0 !important;
    box-shadow: none !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > .z-tabbox .z-center-body {
    padding: 10px 10px 0 !important;
    box-sizing: border-box !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div.z-grid[style*="padding:5px;"] {
    padding: 12px 14px 0 !important;
    background: #fffdfa !important;
    border-radius: 16px 16px 0 0 !important;
    box-sizing: border-box !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div.z-grid[style*="padding:5px;"] > .z-grid-body,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div.z-grid[style*="padding:5px;"] .z-rows,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div.z-grid[style*="padding:5px;"] .z-row,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div.z-grid[style*="padding:5px;"] .z-row-inner,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div.z-grid[style*="padding:5px;"] .z-row-cnt {
    background: transparent !important;
    border: none !important;
    border-radius: 0 !important;
    box-shadow: none !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt > div.z-grid[style*="padding:5px;"] + div[style*="width:100%;text-align:right;"] {
    margin-top: 0 !important;
    padding-top: 8px !important;
    background: #fffdfa !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox),
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> div.z-grid[style*="padding:5px;"]) {
    background: #fffdfa !important;
    border: none !important;
    border-radius: 0 0 14px 14px !important;
    box-shadow: none !important;
    padding: 0 !important;
    overflow: hidden !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) > .z-tabbox,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> div.z-grid[style*="padding:5px;"]) > div.z-grid[style*="padding:5px;"],
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> div.z-grid[style*="padding:5px;"]) > div[style*="width:100%;text-align:right;"] {
    background: transparent !important;
    border: none !important;
    border-radius: 0 !important;
    box-shadow: none !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) > .z-tabbox > .z-tabpanels,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) > .z-tabbox > .z-tabpanels > .z-tabpanel,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) > .z-tabbox > .z-tabpanels > .z-tabpanel > .z-tabpanel-cnt,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-window-embedded,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-window-embedded-cnt-noborder,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-border-layout,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-south,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-south-body,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-grid,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-grid-body,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> div.z-grid[style*="padding:5px;"]) > div.z-grid[style*="padding:5px;"] > .z-grid-body,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> div.z-grid[style*="padding:5px;"]) > div.z-grid[style*="padding:5px;"] .z-rows,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> div.z-grid[style*="padding:5px;"]) > div.z-grid[style*="padding:5px;"] .z-row,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> div.z-grid[style*="padding:5px;"]) > div.z-grid[style*="padding:5px;"] .z-row-inner,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> div.z-grid[style*="padding:5px;"]) > div.z-grid[style*="padding:5px;"] .z-row-cnt {
    background: transparent !important;
    border: none !important;
    border-radius: 0 !important;
    box-shadow: none !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox > .z-listbox-header,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox > .z-listbox-body {
    background: #fffdfa !important;
    border: none !important;
    border-radius: 0 !important;
    box-shadow: none !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox {
    width: 100% !important;
    box-sizing: border-box !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox > .z-listbox-header {
    padding: 0 0 8px !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox > .z-listbox-header > table,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox > .z-listbox-body > table {
    width: 100% !important;
    table-layout: fixed !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-head,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-header,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-header-cnt {
    background: #f4ede4 !important;
    background-image: none !important;
    color: #1b2329 !important;
    border: none !important;
    box-shadow: none !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-header {
    border-right: 1px solid rgba(216, 207, 191, 0.55) !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-header:last-child {
    border-right: none !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-header-cnt,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-cell-cnt {
    width: auto !important;
    white-space: normal !important;
    word-break: break-word !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-item,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-cell,
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-cell-cnt {
    background: transparent !important;
    background-image: none !important;
    color: #1b2329 !important;
    border: none !important;
    box-shadow: none !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-item {
    border-top: 1px solid rgba(216, 207, 191, 0.55) !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-cell {
    padding: 8px 10px !important;
    vertical-align: top !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-header:nth-child(1),
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-cell:nth-child(1) {
    width: 37% !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-header:nth-child(2),
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-cell:nth-child(2) {
    width: 16% !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-header:nth-child(3),
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-cell:nth-child(3) {
    width: 31% !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-header:nth-child(4),
.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox .z-list-cell:nth-child(4) {
    width: 16% !important;
}

.z-window-modal > .z-window-modal-cl > .z-window-modal-cr > .z-window-modal-cm > .z-window-modal-cnt:has(> .z-tabbox) .z-center-body > .z-listbox select.z-listbox {
    width: 100% !important;
    min-height: 32px !important;
    padding: 5px 32px 5px 10px !important;
    border: 1px solid #d8cfbf !important;
    border-radius: 10px !important;
    background-color: #fffdfa !important;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8' viewBox='0 0 12 8'%3E%3Cpath fill='%23232d34' d='M1.41.59 6 5.17 10.59.59 12 2l-6 6-6-6z'/%3E%3C/svg%3E") !important;
    background-repeat: no-repeat !important;
    background-position: right 10px center !important;
    background-size: 12px 8px !important;
    color: #1b2329 !important;
    box-sizing: border-box !important;
    appearance: none !important;
    -webkit-appearance: none !important;
    -moz-appearance: none !important;
    box-shadow: none !important;
}

/* Final exact selectors: form-button + numberbox */
table.form-button.z-button {
    border-collapse: separate !important;
    border-spacing: 0 !important;
    border-radius: 10px !important;
    overflow: hidden !important;
    font-family: var(--ly-font-sans) !important;
}

.z-row-cnt > span.form-button.z-button {
    display: block !important;
    width: 100% !important;
}

.z-row-cnt > span.form-button.z-button > table.form-button.z-button {
    width: 100% !important;
    table-layout: fixed !important;
}

.form-button.z-button-tl,
.form-button.z-button-tm,
.form-button.z-button-tr,
.form-button.z-button-cl,
.form-button.z-button-cm,
.form-button.z-button-cr,
.form-button.z-button-bl,
.form-button.z-button-bm,
.form-button.z-button-br {
    background: #1b2329 !important;
    background-image: none !important;
    color: #f8fbfc !important;
    font-family: var(--ly-font-sans) !important;
}

.form-button.z-button-cm {
    text-align: center !important;
    vertical-align: middle !important;
    white-space: nowrap !important;
    padding: 0 !important;
    line-height: 36px !important;
}

td.editor-button {
    width: 40px !important;
    min-width: 40px !important;
    max-width: 40px !important;
}

button.editor-button,
.editor-button.z-button {
    width: 32px !important;
    min-width: 32px !important;
    max-width: 32px !important;
    height: 32px !important;
    min-height: 32px !important;
}

button.editor-button .z-button-cm,
.editor-button.z-button .z-button-cm {
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='14' height='14' viewBox='0 0 14 14'%3E%3Crect x='2' y='1.5' width='10' height='11' rx='1.5' fill='none' stroke='%2311181d' stroke-width='1.2'/%3E%3Cpath fill='%2311181d' d='M4 3.3h6v1.4H4zM4 6h1.6v1.6H4zm2.2 0h1.6v1.6H6.2zm2.2 0H10v1.6H8.4zM4 8.2h1.6v1.6H4zm2.2 0h1.6v1.6H6.2zm2.2 0H10v1.6H8.4z'/%3E%3C/svg%3E") !important;
    background-repeat: no-repeat !important;
    background-position: center center !important;
    background-size: 14px 14px !important;
    text-indent: -9999px !important;
    overflow: hidden !important;
    padding: 0 !important;
}

button.editor-button img,
.editor-button.z-button img,
.editor-button .z-button-cm img {
    display: none !important;
}
