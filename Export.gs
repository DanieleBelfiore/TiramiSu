var FORMAT_ONELINE   = 'One-line';
var FORMAT_MULTILINE = 'Multi-line';
var FORMAT_PRETTY    = 'Pretty';
var LANGUAGE_JS      = 'JavaScript';
var LANGUAGE_PYTHON  = 'Python';
var STRUCTURE_LIST = 'List';
var STRUCTURE_HASH = 'Hash (keyed by "id" column)';

var DEFAULT_FORMAT = FORMAT_PRETTY;
var DEFAULT_LANGUAGE = LANGUAGE_JS;
var DEFAULT_STRUCTURE = STRUCTURE_LIST;

var FOLDERNAME = 'Tiramisù';
var FILENAME = 'Quotes.json';
var STRUCTURE = { "debug": [], "release": [] };

function onOpen() {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var submenu = [ 
    { name: "Debug Mode", functionName: "exportDataDebug"}, 
    { name: "Release Mode", functionName: "exportDataRelease"}, 
  ];
  ss.addMenu("Export JSON", submenu);
}

function exportDataDebug(e) {
  exportData_(e, false);
}

function exportDataRelease(e) {
  exportData_(e, true);
}

function exportData_(e, releaseMode) {
  var file = null;
  var folders = DriveApp.getFoldersByName(FOLDERNAME);
  if (folders.hasNext()) {
    var folder = folders.next();
    var children = folder.getFilesByName(FILENAME);
    if (children.hasNext()) {
      file = children.next();
    } else {
      SpreadsheetApp.getUi().alert(FILENAME + ' not found.');
      return;
    }
  }

  var ui = SpreadsheetApp.getUi();
  var blob = file.getBlob().getDataAsString();
  var oldContent = JSON.parse(blob);
  var version = releaseMode ? parseInt(oldContent["release"][0]["version"]) + 1 : parseInt(oldContent["debug"][0]["version"]) + 1;
  var prompt = ui.prompt("Please confirm the new version number (" + version + ")");
  var button = prompt.getSelectedButton();
  if (button === ui.Button.OK) {
    version = parseInt(prompt.getResponseText());
  } else if (button === ui.Button.CLOSE) {
    return;
  }
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  var sheets = ss.getSheets();
  var sheetsData = STRUCTURE;
  for (var i = 0; i < sheets.length; i++) {
    var sheet = sheets[i];
    sheet.sort(1).sort(2); // sorting by second column, then by first
    var contents = getRowsData_(sheet, getExportOptions(e));
    sheetsData["debug"][i] = { "contents": contents, "lang": sheet.getName(), "version": version };
    if (releaseMode) {
      sheetsData["release"][i] = sheetsData["debug"][i];
    } else {
      sheetsData["release"][i] = oldContent["release"][i];
    }
  }
  var json = makeJSON_(sheetsData, getExportOptions(e));
  file.setContent(json);
}
  
function getExportOptions(e) {
  var options = {};
  options.language = e && e.parameter.language || DEFAULT_LANGUAGE;
  options.format   = e && e.parameter.format || DEFAULT_FORMAT;
  options.structure = e && e.parameter.structure || DEFAULT_STRUCTURE;
  var cache = CacheService.getPublicCache();
  cache.put('language', options.language);
  cache.put('format', options.format);
  cache.put('structure', options.structure);
  return options;
}

function makeJSON_(object, options) {
  if (options.format == FORMAT_PRETTY) {
    var jsonString = JSON.stringify(object, null, 4);
  } else if (options.format == FORMAT_MULTILINE) {
    var jsonString = Utilities.jsonStringify(object);
    jsonString = jsonString.replace(/},/gi, '},\n');
    jsonString = prettyJSON.replace(/":\[{"/gi, '":\n[{"');
    jsonString = prettyJSON.replace(/}\],/gi, '}],\n');
  } else {
    var jsonString = Utilities.jsonStringify(object);
  }
  if (options.language == LANGUAGE_PYTHON) {
    // add unicode markers
    jsonString = jsonString.replace(/"([a-zA-Z]*)":\s+"/gi, '"$1": u"');
  }
  return jsonString;
}

function getRowsData_(sheet, options) {
  var headersRange = sheet.getRange(1, 1, sheet.getFrozenRows(), sheet.getMaxColumns());
  var headers = headersRange.getValues()[0];
  var dataRange = sheet.getRange(sheet.getFrozenRows() + 1, 1, sheet.getMaxRows(), sheet.getMaxColumns());
  var objects = getObjects_(dataRange.getValues(), normalizeHeaders_(headers));
  objects = objects.filter(f => f.text);
  if (options.structure == STRUCTURE_HASH) {
    var objectsById = {};
    objects.forEach(function(object) {
      objectsById[object.id] = object;
    });
    return objectsById;
  } else {
    return objects;
  }
}

function getObjects_(data, keys) {
  var objects = [];
  for (var i = 0; i < data.length; ++i) {
    var object = {};
    var hasData = false;
    for (var j = 0; j < data[i].length; ++j) {
      var cellData = data[i][j];
      if (isCellEmpty_(cellData)) {
        continue;
      }
      object[keys[j]] = cellData;
      hasData = true;
    }
    if (hasData) {
      objects.push(object);
    }
  }
  return objects;
}

function normalizeHeaders_(headers) {
  var keys = [];
  for (var i = 0; i < headers.length; ++i) {
    var key = normalizeHeader_(headers[i]);
    if (key.length > 0) {
      keys.push(key);
    }
  }
  return keys;
}

function normalizeHeader_(header) {
  var key = "";
  var upperCase = false;
  for (var i = 0; i < header.length; ++i) {
    var letter = header[i];
    if (letter == " " && key.length > 0) {
      upperCase = true;
      continue;
    }
    if (!isAlnum_(letter)) {
      continue;
    }
    if (key.length == 0 && isDigit_(letter)) {
      continue; // first character must be a letter
    }
    if (upperCase) {
      upperCase = false;
      key += letter.toUpperCase();
    } else {
      key += letter.toLowerCase();
    }
  }
  return key;
}

function isCellEmpty_(cellData) {
  return typeof(cellData) == "string" && cellData == "";
}

function isAlnum_(char) {
  return char >= 'A' && char <= 'Z' ||
    char >= 'a' && char <= 'z' ||
    isDigit_(char);
}

function isDigit_(char) {
  return char >= '0' && char <= '9';
}