function findCompound(searchMode, compoundData) {
    var data = {
        searchMode: searchMode,
        compoundData: compoundData
    };
    $.ajax({
        url: "/findCompound",
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        data: JSON.stringify(data),
        success: function(responseText, status, jqXhr) {
            displayResults(jqXhr.responseText);
        },
        error: function(jqXhr, status) {
            displayResults("findCompound error: " + jqXhr.responseText, true);
        }
    });
}

function deleteCompound(compoundId) {
    var data = {
        compoundID: compoundId
    };
    $.ajax({
        url: "/deleteCompound",
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        data: JSON.stringify(data),
        success: function(responseText, status, jqXhr) {
            displayResults("delete success");
        },
        error: function(jqXhr, status) {
            displayResults("deleteCompound error: " + jqXhr.responseText, true);
        }
    });
}

function modifyCompound(compoundId, compoundData) {
    var data = {
        compoundID: compoundId,
        compoundData: compoundData
    };
    $.ajax({
        url: "/modifyCompound",
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        data: JSON.stringify(data),
        success: function(responseText, status, jqXhr) {
            displayResults("modify success");
        },
        error: function(jqXhr, status) {
            displayResults("modifyCompound error: " + jqXhr.responseText, true);
        }
    });
}

function saveCompound(compoundData) {
    var data = {
        compoundData: compoundData
    };
    $.ajax({
        url: "/saveCompound",
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        data: JSON.stringify(data),
        success: function(responseText, status, jqXhr) {
            displayResults(jqXhr.responseText);
        },
        error: function(jqXhr, status) {
            displayResults("saveCompound error: " + jqXhr.responseText, true);
        }
    });
}

function pubChemLoad(pubChemMode, pubChemInt) {
    if (pubChemMode == "LoadID"){
        var data = {
            pubchemID: pubChemInt
        }
        $.ajax({
            url: "/PubChem/loadID",
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            data: JSON.stringify(data),
            success: function(responseText, status, jqXhr) {
                displayResults("save success");
            },
            error: function(jqXhr, status) {
                displayResults("PubChem load Int error: " + jqXhr.responseText, true);
            }
        })
    } else {
        var data = {
            numCompounds: pubChemInt
        }
        $.ajax({
            url: "/PubChem/loadRand",
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            data: JSON.stringify(data),
            success: function(responseText, status, jqXhr) {
                displayResults("save success");
            },
            error: function(jqXhr, status) {
                displayResults("PubChem load Rand error: " + jqXhr.responseText, true);
            }
        });
    }
}

function init() {
    $(".find-compound-section .execute").on("click", function(e) {
        findCompound($(".find-compound-section .search-mode").val(), $(".find-compound-section .compound-data").val());
    });
    $(".delete-compound-section .execute").on("click", function(e) {
        deleteCompound($(".delete-compound-section .compound-id").val());
    });
    $(".modify-compound-section .execute").on("click", function(e) {
        modifyCompound($(".modify-compound-section .compound-id").val(), $(".modify-compound-section .compound-data").val());
    });
    $(".save-compound-section .execute").on("click", function(e) {
        saveCompound($(".save-compound-section .compound-data").val());
    });
    $(".pubchem-section .execute").on("click", function(e) {
        pubChemLoad($(".pubchem-section .pubchem-mode").val(), $(".pubchem-section .pubchem-int").val());
    });
}

function displayResults(outputString, isError){
    $(".output-section .results").val(outputString);
    if (isError){$(".output-section .results").addClass("error")} else
        {$(".output-section .results").removeClass("error")}
}
$(document).ready(init);
