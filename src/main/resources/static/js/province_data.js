$(document).ready(function (e) {
    $.ajax({
        url: 'https://provinces.open-api.vn/api/',
        method: 'GET',
        success: function (data) {
            data.forEach(element => {
                var key = element.code;
                var value = element.name;
                $('#province').append($("<option></option>")
                    .attr("value", key)
                    .text(value));

            });

        }
    })

})
$('#province').change(function (e) {
    var code = $(this).val()
    getListDistrict(code)

})
$('#district').change(function (e) {
    var code = $(this).val()
    getListWard(code)
})

function getListDistrict(code) {
    $.ajax({
        url: 'https://provinces.open-api.vn/api/p/' + code + '?depth=2',
        method: 'GET',
        success: function (data) {
            $('#district').empty()
            data.districts.forEach(element => {
                var key = element.code
                var value = element.name
                $('#district').append($("<option></option>")
                    .attr("value", key)
                    .text(value))
            })
            getListWard(data.districts[0].code)
        }
    })
}

function getListWard(code) {
    $.ajax({
        url: 'https://provinces.open-api.vn/api/d/' + code + '?depth=2',
        method: 'GET',
        success: function (data) {
            $('#ward').empty()
            data.wards.forEach(element => {
                var key = element.code
                var value = element.name
                $('#ward').append($("<option></option>")
                    .attr("value", key)
                    .text(value))
            })
        }
    })

}