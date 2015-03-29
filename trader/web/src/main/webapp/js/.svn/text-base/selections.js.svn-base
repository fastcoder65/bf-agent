function doSelectAll(doc) {
    setSelected(doc, true);
}

function doUnSelectAll(doc) {
    setSelected(doc, false);
}

function setSelected(doc, isSelected) {
    var INPUTs = ((doc) ? doc : document).getElementsByTagName("input");
    for (var i = 0; i < INPUTs.length; i++) {
        if ((INPUTs[i].type == "checkbox") && (INPUTs[i].name.indexOf("select") != -1)) {
            INPUTs[i].checked = isSelected;
        }
    }
}

function doInvertSelection(doc) {
    var INPUTs = ((doc) ? doc : document).getElementsByTagName("input");
    for (var i = 0; i < INPUTs.length; i++) {
        if ((INPUTs[i].type == "checkbox") && (INPUTs[i].name.indexOf("select") != -1)) {
            INPUTs[i].checked = !(INPUTs[i].checked);
        }
    }
}

function swSelectCheckBox(thisCheckBox) {
    if (thisCheckBox.checked) {
        doSelectAll(thisCheckBox.document);
    } else {
        doUnSelectAll(thisCheckBox.document);
    }
}

function checkSelection(thisForm) {
   var result = false;
     if (thisForm != null) {
         var formElements = thisForm.elements;
         for (var i = 0; i < formElements.length; i++) {
             if (formElements[i].type == "checkbox" && (formElements[i].name.indexOf("selected") != -1) ) {
               var _checked = formElements[i].checked;
               if (_checked)
               { result = true;   break;}
             }
         }
     }
    return result;
}

function validateChecks(thisForm) {
    var result = checkSelection(thisForm);
    if (!result) {
        alert(sNoChecksFound);
    }
    return result;
}