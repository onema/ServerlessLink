window.addEventListener("load", function () {
    function sendData() {
        var XHR = new XMLHttpRequest();

        // Bind the FormData object and the form element
        var FD = new FormData(form);
        var object = {};
        FD.forEach(function(value, key){
            object[key] = value;
        });
        var json = JSON.stringify(object);

        // Define what happens on successful data submission
        XHR.addEventListener("load", function(event) {
            // alert(event.target.responseText);
            var obj = JSON.parse(event.target.responseText);
            document.getElementById("response").innerHTML = _config.api.scheme + "://" + obj.url;
            // document.getElementById("responseCopyButton").innerHTML = "<button onclick=\"copy()\">Copy URL</button>";
        });

        // Define what happens in case of error
        XHR.addEventListener("error", function(event) {
            alert('Oops! Something went wrong. ');
            document.getElementById("response").innerHTML = "";
        });

        // Set up our request
        XHR.open("POST",  _config.api.invokeUrl);

        // The data sent is what the user provided in the form
        XHR.send(json);

        var location = XHR.getResponseHeader("location");
        console.log(location)
    }

    function copy() {
        /* Get the text field */
        var copyText = document.getElementById("response");

        /* Select the text field */
        copyText.select();

        /* Copy the text inside the text field */
        document.execCommand("copy");

        /* Alert the copied text */
        alert("Copied URL: " + copyText.value);
    }

    // Access the form element...
    var form = document.getElementById("generate-link");

    // ...and take over its submit event.
    form.addEventListener("submit", function (event) {
        event.preventDefault();
        sendData();
        document.getElementById("response").innerHTML = "<img class='loading' src='images/loading.gif'>";
    });
});