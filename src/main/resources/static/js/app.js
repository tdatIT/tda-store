$("#style-form").submit(function (e) {
    e.preventDefault()

    var form = $(this);

    $.ajax({
        url: '/admin/san-pham/them-style',
        method: 'post',
        data: form.serialize(),
        success: function (data) {
            $('#style-form')[0].reset();
            iziToast.success({
                title: 'Thành công',
                message: 'Thêm thành công loại cho sản phẩm',
                position: 'topRight'
            });
        },
        error: function (data) {
            iziToast.warning({
                title: 'Thất bại',
                message: 'Có lỗi xảy ra có thể do sản phẩm không tồn tại',
                position: 'topRight'
            });
        }
    })
})
$('.action-type').on('click', function (e) {
    swal({
        title: 'Bạn có muốn xóa option cho sản phẩm này?',
        text: 'Đồng ý để xác nhận xóa',
        icon: 'warning',
        buttons: true,
        dangerMode: true,
    }).then((willDelete) => {
        if (willDelete) {
            var type = $(this).closest('tr').children('td:first').text()
            $.ajax({
                url: '/admin/san-pham/style/xoa',
                method: 'POST',
                data: {id: type},
                success: function () {
                    location.reload()
                },
                error: function () {
                    swal("Thất bại !", "Xóa sản phẩm không thành công thử lại sau !", "error");
                }
            })
        } else {
            swal('Hủy thao tác thành công');
        }
    });
})
$('#remove-product').click(function (e) {
    swal({
        title: 'Bạn có muốn xóa sản phẩm này?',
        text: 'Sản phẩm sẽ không hiển thị trên web nhưng vẫn lưu trử dữ liệu trong hệ thống',
        icon: 'warning',
        buttons: true,
        dangerMode: true,
    }).then((willDelete) => {
        if (willDelete) {
            var code = $('#productCode').val()
            $.ajax({
                url: '/admin/san-pham/xoa',
                method: 'POST',
                data: {productCode: code},
                success: function () {
                    window.location.href = '/admin/san-pham'
                },
                error: function () {
                    swal("Thất bại !", "Xóa sản phẩm không thành công thử lại sau !", "error");
                }
            })
        } else {
            swal('Hủy thao tác thành công');
        }
    });
})