
function GetCookie(name) { 
	var arg = name + "="; 
	var alen = arg.length; 
	var clen = window.document.cookie.length; 
	var i = 0; 
	while (i < clen) { 
		var j = i + alen; 
		if (window.document.cookie.substring(i, j) == arg) return getCookieVal (j); 
		i = window.document.cookie.indexOf(" ", i) + 1; 
		if (i == 0) break; 
	} 
	return null; 
} 

function getCookieVal (offset) { 
	var endstr = window.document.cookie.indexOf (";", offset); 
	if (endstr == -1) endstr = window.document.cookie.length; 
	return unescape(window.document.cookie.substring(offset, endstr)); 
} 

var __ranknow_tj_loaded;
if(__ranknow_tj_loaded == null)
{
	__ranknow_tj_loaded = (new Date().getTime());
// ERROR003: ����RankNow������������������2017-05-16���ڣ���������ͣ���뾡����ϵ�������¿�ͨ��
}