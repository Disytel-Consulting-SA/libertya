/****************************************************************************
 * Compiere (c) Jorg Janke - All rights reseverd
 * $Id: wstore.js,v 1.9 2004/11/09 03:50:11 jjanke Exp $
 *
 * Web Store Scripts
 ***************************************************************************/

var mandatory = "Enter mandatory:";

/**
 *	Is field empty ?
 *  Returns true if field is empty
 */
function isEmpty (value)
{
	if (value == null)
		return true;
	if (value == "")
		return true;
	for (var i = 0; i < value.length; i++)
	{
		var c = value.charAt(i);
		if ((c != ' ' && c != '\n' && c != '\t'))
			return false;
	}
	return true;
}



function checkCreditCard(field)
{
	window.alert(field.name + "=" + field.value);
	return true;
}

function checkExpDate(field)
{
	window.alert(field.name + "=" + field.value);
	return true;
}

function checkABA (field)
{
	window.alert(field.name + "=" + field.value);
	return true;
}

function checkBAcct (field)
{
	window.alert(field.name + "=" + field.value);
	return true;
}
function checkChknum (field)
{
	window.alert(field.name + "=" + field.value);
	return true;
}

function checkDL (field)
{
	window.alert(field.name + "=" + field.value);
	return true;
}

function checkField (field)
{
	window.alert(field.name + "=" + field.value);
	return true;
}


/**
 * 	Test mandatory fields for lookup
 */
function checkLookup (field)
{
	window.alert (field);
	var f = field.form;
	window.alert (f);
	if (!isEmpty(f.EMAIL.value) && !isEmpty(f.password.value))
		return true;
	var msg = mandatory;
	if (isEmpty(f.EMAIL.value))
		mandatory += "\n - " + f.EMAIL.title;
	if (isEmpty(f.password.value))
		mandatory += "\n - " + f.password.title;
	window.alert(mandatory);
	return false;
}

var statusInfo = '';

/****************************************************************************
 * 	Check form
 *	- onSubmit="submitForm(this, new Array ('Name','..'));"
 */
function checkForm (formObj, requiredFields)
{
	statusInfo += 'checkForm:' + formObj.name + '[' + requiredFields.length + ']';
	if (formObj.nodeName == 'FORM')
	{
		if (formObj.Submit)
		{
			formObj.Submit.disabled=true;
			statusInfo += '(' + formObj.Submit.name + ')';
		}
	}
	else
	{
		formObj = formObj.form;
		if (formObj == null | formObj.nodeName != 'FORM')
		{
			alert ('invalid submitter');
			return false;
		}
		statusInfo += '->' + formObj.name;
	}
	window.status=statusInfo;
	
	var alertMsg = "";
	//	check required fields
	if (requiredFields)
	{
		for (i=0; i<requiredFields.length; i++)
		{
			formElemLength = eval ("formObj." + requiredFields[i] + ".value.length");
			if (formElemLength == 0)
				alertMsg += "- " + requiredFields[i] + "\n";
		}
	}
	//	show Error Message
	if (alertMsg.length)
	{
		if (formObj.Submit)
			formObj.Submit.disabled=false;
		alertMsg = mandatory + "\n" + alertMsg;
		alert (alertMsg);
		return false;
	}
	//	Switch on processing
	if (document.getElementById('submitDiv'))
	{
		document.getElementById('submitDiv').style.display='none';
		document.getElementById('processingDiv').style.display='inline';
	}
	//
	statusInfo += ' done - ';
//	window.status=statusInfo;
	return true;
}	//	submitForm

/**
 * Pop up Window
 */
function popUp(url) 
{
	sealWin=window.open(url,"win",'toolbar=0,location=0,directories=0,status=1,menubar=1,scrollbars=1,resizable=1,width=500,height=450');
	self.name = "mainWin";
}
