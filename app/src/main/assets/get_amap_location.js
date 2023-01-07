(function () {
    //https://lbs.amap.com/tools/picker
    var inputResult = document.querySelector("#txtCoordinate");
    var lon = null;
    var lat = null;
    var errorValue = null;
    try {
        var text = inputResult.value;
        var locations = text.split(',');
        lon = locations[0].trim();
        lat = locations[1].trim();
        //验证格式
        parseFloat(lon);
        parseFloat(lat);
    } catch (error) {
        console.error(error);
        errorValue = error;
    }
    return {
        "lon": lon,
        "lat": lat,
        "error":errorValue
    }
})();

