/**
 * Agrega un producto al carrito asumiendo cantidad = 1.
 * @param {HTMLFormElement} formulario - El objeto form que contiene el ID del producto.
 */
function addCart(formulario) {
    // 1. Obtención de datos y ruta (solo el ID del producto)
    var idProducto = $(formulario).find('input[name="idProducto"]').val();
    var ruta = $(formulario).attr('action') || '/carrito/agregar'; // Lee la ruta del atributo 'action'

    // 2. Seguridad (CSRF Token)
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    // 3. Petición AJAX (solo se envía el ID del producto)
    $.ajax({
        url: ruta,
        type: 'POST',
        data: {
            // ** CRÍTICO: SOLO ENVIAMOS idProducto **
            idProducto: idProducto
        },
        beforeSend: function (xhr) {
            if (csrfHeader && csrfToken) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            }
        },
        success: function (response) {
            // Update cart badge
            actualizarBadgeCarrito();
            console.log("Producto agregado con cantidad por defecto (1).");
        },
        error: function (xhr, status, error) {
            // Manejo de errores
            var mensaje = xhr.responseText || 'Error en la conexión.';
            alert("Error al agregar producto: " + mensaje);
        }
    });
}

/**
 * Actualiza el badge del carrito con la cantidad de items
 */
function actualizarBadgeCarrito() {
    $.ajax({
        url: '/carrito/cantidad',
        type: 'GET',
        success: function (cantidad) {
            var badge = $('#cartBadge');
            if (cantidad > 0) {
                badge.text(cantidad);
                badge.show();
            } else {
                badge.hide();
            }
        },
        error: function () {
            console.log("Error al obtener cantidad del carrito");
        }
    });
}

/**
 * Maneja el click en el carrito - redirige a login si no está autenticado
 */
function handleCartClick(event) {
    // Esta función solo se ejecuta si el usuario NO está autenticado
    // Si está autenticado, el link normal funciona
    return true;
}

// Actualizar badge al cargar la página
$(document).ready(function() {
    actualizarBadgeCarrito();
});

// funcion para hacer un preview de una imagen 
function mostrarImagen(input) {
    if (input.files && input.files[0]) {
        const imagen = input.files[0];
        const maximo = 512 * 1024; //Se limita el tamaño a 512 Kb las imágenes.
        if (imagen.size <= maximo) {
            var lector = new FileReader();
            lector.onload = function (e) {
                $('#blah').attr('src', e.target.result).height(200);
            };
            lector.readAsDataURL(input.files[0]);
        } else {
            alert("La imagen seleccionada es muy grande... no debe superar los 512 Kb!");
        }
    }
}

//Para insertar información en el modal según el registro...
document.addEventListener('DOMContentLoaded', function () {
    const confirmModal = document.getElementById('confirmModal');
    if (confirmModal) {
        confirmModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            document.getElementById('modalId').value = button.getAttribute('data-bs-id');
            document.getElementById('modalDescripcion').textContent = button.getAttribute('data-bs-descripcion');
        });
    }
});

//Para quitar toast
setTimeout(() => {
    document.querySelectorAll('.toast').forEach(t => t.classList.remove('show'));
}, 4000);
