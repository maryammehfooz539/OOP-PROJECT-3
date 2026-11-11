/* app.js - small JavaScript used by every page of Gemora (written for this project).
   1. Sidebar open/close with the 3-line (hamburger) button
   2. Quantity boxes with  -  and  +  buttons
   3. Input filters (digits only, letters only ...) and form validation messages
   4. Password show/hide, and "Add to Cart" without reloading the page */
(function () {
    'use strict';
    var body = document.body;

    /* ---------- 1. Sidebar ---------- */
    var STORE_KEY = 'gemoraSidebarHidden';
    var toggle = document.getElementById('menuToggle');
    var backdrop = document.getElementById('sidebarBackdrop');
    function isMobile() { return window.innerWidth <= 991; }
    function closeMobileMenu() { body.classList.remove('sidebar-open'); }
    if (toggle) {
        toggle.addEventListener('click', function () {
            if (isMobile()) {
                body.classList.toggle('sidebar-open');
            } else {
                body.classList.toggle('sidebar-hidden');
                try { localStorage.setItem(STORE_KEY, body.classList.contains('sidebar-hidden') ? '1' : '0'); } catch (e) { /* storage not available */ }
            }
        });
    }
    if (backdrop) { backdrop.addEventListener('click', closeMobileMenu); }
    document.addEventListener('keydown', function (e) { if (e.key === 'Escape') { closeMobileMenu(); } });

    /* ---------- Warning when a wrong character is typed ---------- */
    // Shows a red message under the box for 3 seconds, for example "You can not enter characters".
    function warn(el, text) {
        var anchor = el.closest('.qty-stepper') || el;
        if (el._warn) { el._warn.remove(); }
        clearTimeout(el._warnTimer);
        var div = document.createElement('div');
        div.className = 'field-warning';
        div.textContent = text;
        anchor.insertAdjacentElement('afterend', div);
        el._warn = div;
        el._warnTimer = setTimeout(function () { div.remove(); el._warn = null; }, 3000);
    }
    var MSG_DIGITS = 'You can not enter characters. Only numbers (0-9) are allowed.';
    var MSG_DECIMAL = 'You can not enter letters or symbols here. Only numbers are allowed.';
    var MSG_ALLOW = 'This character is not allowed here.';

    /* ---------- 2. Quantity boxes ( -  [ 3 ]  + ) ---------- */
    document.querySelectorAll('.qty-stepper').forEach(function (box) {
        var input = box.querySelector('.qty-input');
        var minus = box.querySelector('[data-step="-1"]');
        var plus = box.querySelector('[data-step="1"]');
        var min = parseInt(box.getAttribute('data-min') || '1', 10);
        var max = parseInt(box.getAttribute('data-max') || '100000', 10);
        var timer = null;

        function current() {
            var v = parseInt(input.value, 10);
            return isNaN(v) ? min : v;
        }
        function refresh() {
            var v = current();
            if (minus) { minus.disabled = v <= min; }
            if (plus) { plus.disabled = v >= max; }
        }
        function setValue(v) {
            input.value = Math.max(min, Math.min(max, v));
            refresh();
            input.dispatchEvent(new Event('input', { bubbles: true }));
            // in the cart page the box saves itself 0.5 second after the last click
            if (box.getAttribute('data-autosubmit') === 'true' && input.form) {
                clearTimeout(timer);
                timer = setTimeout(function () { input.form.submit(); }, 500);
            }
        }
        if (minus) { minus.addEventListener('click', function () { setValue(current() - 1); }); }
        if (plus) { plus.addEventListener('click', function () { setValue(current() + 1); }); }
        input.addEventListener('input', function () {
            var cleaned = input.value.replace(/\D/g, '').slice(0, String(max).length);
            if (cleaned !== input.value) {
                if (/\D/.test(input.value)) { warn(input, MSG_DIGITS); }
                input.value = cleaned;
            }
            refresh();
        });
        input.addEventListener('blur', function () { setValue(current()); });
        refresh();
    });

    /* ---------- 3. Input filters (a wrong character is removed AND a warning is shown) ---------- */
    // data-only="digits"  -> only numbers (phone, quantity). data-warn="..." can change the message.
    document.querySelectorAll('[data-only="digits"]').forEach(function (el) {
        function clean(show) {
            var c = el.value.replace(/\D/g, '');
            if (c !== el.value) { el.value = c; if (show) { warn(el, el.getAttribute('data-warn') || MSG_DIGITS); } }
        }
        el.addEventListener('input', function () { clean(true); });
        // when the box is already full, one more digit is blocked by the browser: tell the user why
        el.addEventListener('keydown', function (e) {
            var max = parseInt(el.getAttribute('maxlength') || '0', 10);
            if (max > 0 && /^[0-9]$/.test(e.key) && el.value.length >= max && el.selectionStart === el.selectionEnd) {
                warn(el, 'You can enter at most ' + max + ' digits here.');
            }
        });
        clean(false);
    });
    // data-only="decimal" -> numbers with one dot and at most 2 digits after it (price, weight, rates)
    document.querySelectorAll('[data-only="decimal"]').forEach(function (el) {
        function clean(show) {
            var c = el.value.replace(/[^0-9.]/g, '');
            var dot = c.indexOf('.');
            if (dot >= 0) { c = c.slice(0, dot + 1) + c.slice(dot + 1).replace(/\./g, '').slice(0, 2); }
            if (c !== el.value) { el.value = c; if (show) { warn(el, el.getAttribute('data-warn') || MSG_DECIMAL); } }
        }
        el.addEventListener('input', function () { clean(true); });
        clean(false);
    });
    // data-allow="..." -> only these characters are accepted (names, addresses ...)
    document.querySelectorAll('[data-allow]').forEach(function (el) {
        var bad = new RegExp('[^' + el.getAttribute('data-allow') + ']', 'gu');
        function clean(show) {
            var c = el.value.replace(bad, '');
            if (c !== el.value) { el.value = c; if (show) { warn(el, el.getAttribute('data-warn') || MSG_ALLOW); } }
        }
        el.addEventListener('input', function () { clean(true); });
        el.addEventListener('blur', function () { el.value = el.value.replace(/\s+/g, ' ').trim(); });
        clean(false);
    });
    // data-minval / data-maxval -> number range check for decimal boxes
    document.querySelectorAll('[data-minval],[data-maxval]').forEach(function (el) {
        function check() {
            var v = parseFloat(el.value);
            var min = el.hasAttribute('data-minval') ? parseFloat(el.getAttribute('data-minval')) : -Infinity;
            var max = el.hasAttribute('data-maxval') ? parseFloat(el.getAttribute('data-maxval')) : Infinity;
            if (el.value !== '' && (isNaN(v) || v < min || v > max)) {
                el.setCustomValidity(el.getAttribute('data-msg') || 'Value is out of range.');
            } else { el.setCustomValidity(''); }
        }
        el.addEventListener('input', check); check();
    });

    /* ---------- Validation messages under the fields ---------- */
    function controlsOf(form) { return form.querySelectorAll('input:not([type=hidden]):not([type=file]), select, textarea'); }
    function anchorOf(el) { var box = el.closest('.qty-stepper'); return box ? box : el; }
    function clearError(el) {
        var a = anchorOf(el);
        // remove the red error message (a typing warning next to it is left alone)
        var next = a.nextElementSibling;
        while (next && (next.classList.contains('field-error') || next.classList.contains('field-warning'))) {
            var after = next.nextElementSibling;
            if (next.classList.contains('field-error')) { next.remove(); }
            next = after;
        }
        el.classList.remove('is-invalid');
    }
    function showError(el) {
        clearError(el);
        if (el.checkValidity()) { return; }
        var div = document.createElement('div');
        div.className = 'field-error';
        div.textContent = el.getAttribute('data-msg') && el.validity.customError ? el.getAttribute('data-msg')
            : (el.getAttribute('data-msg') || el.validationMessage);
        anchorOf(el).insertAdjacentElement('afterend', div);
        el.classList.add('is-invalid');
    }
    document.querySelectorAll('form.needs-validation').forEach(function (form) {
        form.setAttribute('novalidate', 'novalidate');
        form.addEventListener('submit', function (e) {
            var first = null;
            controlsOf(form).forEach(function (el) {
                if (el.type === 'text') { el.value = el.value.trim(); }
                showError(el);
                if (!el.checkValidity() && !first) { first = el; }
            });
            if (first) { e.preventDefault(); first.focus(); }
        });
        controlsOf(form).forEach(function (el) {
            el.addEventListener('input', function () { if (el.classList.contains('is-invalid')) { showError(el); } });
            el.addEventListener('change', function () { if (el.classList.contains('is-invalid')) { showError(el); } });
        });
    });

    /* ---------- 4. Password eye ---------- */
    document.querySelectorAll('[data-toggle-password]').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var input = document.getElementById(btn.getAttribute('data-toggle-password'));
            if (!input) { return; }
            var show = input.type === 'password';
            input.type = show ? 'text' : 'password';
            btn.innerHTML = show ? '<i class="bi bi-eye-slash"></i>' : '<i class="bi bi-eye"></i>';
        });
    });

    /* ---------- 5. Add to Cart (stays on the same page) ---------- */
    function toast(text, bad) {
        var t = document.createElement('div');
        t.className = 'gemora-toast' + (bad ? ' bad' : '');
        t.textContent = text;
        document.body.appendChild(t);
        setTimeout(function () { t.classList.add('show'); }, 10);
        setTimeout(function () { t.classList.remove('show'); setTimeout(function () { t.remove(); }, 300); }, 2200);
    }
    document.querySelectorAll('form.js-add-cart').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            e.preventDefault();
            var btn = form.querySelector('button');
            btn.disabled = true;
            // getAttribute: the form has a hidden field named "action", so form.action would not be the web address
            fetch(form.getAttribute('action'), {
                method: 'POST',
                credentials: 'same-origin',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded', 'X-Requested-With': 'fetch' },
                body: new URLSearchParams(new FormData(form)).toString()
            }).then(function (r) { return r.json(); }).then(function (d) {
                document.querySelectorAll('.cart-count').forEach(function (b) { b.textContent = d.count; });          // sidebar: pieces in all carts
                document.querySelectorAll('.cart-count-mine').forEach(function (b) { b.textContent = d.mine; });  // this customer's cart
                toast(d.message, !d.ok);
                btn.disabled = false;
            }).catch(function () { form.submit(); });   // if anything fails, do a normal page submit
        });
    });

    /* ---------- 6. Sidebar: the mouse wheel scrolls only the sidebar, never the page behind it ---------- */
    var sidebarBox = document.querySelector('.sidebar');
    if (sidebarBox) {
        sidebarBox.addEventListener('wheel', function (e) {
            e.preventDefault();
            sidebarBox.scrollTop += e.deltaMode === 1 ? e.deltaY * 16 : e.deltaY;
        }, { passive: false });
    }

    /* ---------- 7. Back button: go to the page we came from (or to its fallback address) ---------- */
    var backButton = document.getElementById('backBtn');
    if (backButton) {
        backButton.addEventListener('click', function (e) {
            var cameFromHere = document.referrer && document.referrer.indexOf(window.location.origin) === 0;
            if (cameFromHere && window.history.length > 1) {
                e.preventDefault();
                window.history.back();
            }   // otherwise the link works normally and opens its fallback page
        });
    }
})();
