function changeAll(formId, checked) {
	var form = document.getElementById(formId);
	for (var i = 0 ; i < form.elements.length ; i++) {
		var x = form.elements[i];
		if (x.name == "exportForm:columns")
			x.checked = checked;
	}
}