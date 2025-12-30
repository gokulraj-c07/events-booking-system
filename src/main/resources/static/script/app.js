// Initialize
document.addEventListener('DOMContentLoaded', () => {
    setupMobileMenu();
    setupUserDropdown();
    setupSuccessMessage();
    setupCalendar();
    setupUserDashboard();
    setupProviderDashboard();
	setupFormInitialState();
	setupProviderCheckStatus();
	setupProviderBill();
});
// -----------------------------------------
// Mobile Menu Toggle
// -----------------------------------------
function setupMobileMenu() {
    const btn = document.getElementById('mobile-menu-btn');
    const menu = document.getElementById('mobile-menu');
    if (!btn || !menu) return;
    btn.addEventListener('click', () => {
        menu.classList.toggle('hidden');
        const icon = btn.querySelector('div');
        icon.className = menu.classList.contains('hidden')
            ? 'icon-menu text-2xl text-[var(--text-primary)]'
            : 'icon-x text-2xl text-[var(--text-primary)]';
    });
}
// -----------------------------------------
// Desktop Hover Dropdown + Mobile Click Dropdown
// -----------------------------------------
function setupUserDropdown() {
    // Desktop (Hover)
    const desktopBtn = document.getElementById("userMenuButtonDesktop");
    const desktopMenu = document.getElementById("userDropdownDesktop");
    if (desktopBtn && desktopMenu) {
        desktopBtn.addEventListener("mouseenter", () => desktopMenu.classList.remove("hidden"));
        desktopBtn.addEventListener("mouseleave", () => {
            setTimeout(() => {
                if (!desktopMenu.matches(":hover")) desktopMenu.classList.add("hidden");
            }, 200);
        });
        desktopMenu.addEventListener("mouseleave", () => desktopMenu.classList.add("hidden"));
    }
    // Mobile (Click)
    const mobileBtn = document.getElementById("userMenuButtonMobile");
    const mobileMenu = document.getElementById("userDropdownMobile");

    if (mobileBtn && mobileMenu) {
        mobileBtn.addEventListener("click", () => {
            mobileMenu.classList.toggle("hidden");
        });
    }
}

// -----------------------------------------
// Auto Hide Success Message
// -----------------------------------------
function setupSuccessMessage() {
    const msg = document.getElementById('success-msg');
    if (!msg) return;

    setTimeout(() => msg.style.display = 'none', 4000);
}
// -----------------------------------------
// Disable Past Dates in Calendar (if exist)
// -----------------------------------------
function setupCalendar() {
    const input = document.getElementById('event-date');
    if (!input) return;

    const today = new Date().toISOString().split('T')[0];
    input.setAttribute('min', today);
}

// --------------------------------------------------------------------
// USER DASHBOARD (profile / bookings / bills)
// --------------------------------------------------------------------
function setupUserDashboard() {
    const menuItems = document.querySelectorAll(".menu-item");
    const sections = document.querySelectorAll(".content-section");
	const modal = document.getElementById("bookingModal");
	const closeBtn = document.getElementById("closeModal");
	const buttons = document.querySelectorAll(".view-booking-btn");

    if (!menuItems.length || !sections.length) return;

    menuItems.forEach(item => {
        item.addEventListener("click", () => {
            const section = item.dataset.section;

            menuItems.forEach(m => m.classList.remove("active"));
            sections.forEach(s => s.classList.remove("active"));

            item.classList.add("active");
            document.getElementById(section + "-section").classList.add("active");
        });
    });

    // Profile image preview
    const profileUpload = document.getElementById("profile-upload");
    const previewPic = document.getElementById("preview-pic");

    if (profileUpload && previewPic) {
        profileUpload.addEventListener("change", (e) => {
            const file = e.target.files[0];
            if (!file) return;
            previewPic.src = URL.createObjectURL(file);
        });
    }
	
	buttons.forEach(btn => {
	    btn.addEventListener("click", async () => {
	
	        const id = btn.dataset.id;
	
	        const response = await fetch(`/userdashboard/view-booking/${id}`);
	        if (!response.ok) return;
	
	        const data = await response.json();
	
	        document.getElementById("mService").textContent = data.serviceName;
	        document.getElementById("mCategory").textContent = data.category;
	        document.getElementById("mLocation").textContent = data.location;
	        document.getElementById("mProvider").textContent = data.providerName;
	        document.getElementById("mEmail").textContent = data.businessEmailId;
	        document.getElementById("mPhone").textContent = data.businessPhoneNumber;
	        document.getElementById("mDate").textContent = data.bookingDate;
	        document.getElementById("mStatus").textContent = data.status;
	
	        modal.classList.remove("hidden");
	        modal.classList.add("flex");  // Ensure centered alignment
	    });
	});
	
	if (closeBtn) {
	    closeBtn.addEventListener("click", () => {
	        modal.classList.add("hidden");
	        modal.classList.remove("flex");
	    });
	}
	
	// CANCEL BOOKING FUNCTIONALITY
	(function setupCancelBooking() {
	    const cancelButtons = document.querySelectorAll('.cancel-booking-btn');
	    
	    // Check each cancel button and hide if within 24 hours
	    cancelButtons.forEach(btn => {
	        const bookingDateStr = btn.dataset.date;
	        if (bookingDateStr) {
	            const bookingDate = new Date(bookingDateStr);
	            const now = new Date();
	            const hoursDiff = (bookingDate - now) / (1000 * 60 * 60);
	            
	            // Hide cancel button if event is within 24 hours
	            if (hoursDiff < 24) {
	                btn.style.display = 'none';
	            }
	        }
	        
	        // Handle cancel button click
	        btn.addEventListener('click', async () => {
	            const bookingId = btn.dataset.id;
	            const bookingDateStr = btn.dataset.date;
	            
	            // Double check 24-hour rule
	            if (bookingDateStr) {
	                const bookingDate = new Date(bookingDateStr);
	                const now = new Date();
	                const hoursDiff = (bookingDate - now) / (1000 * 60 * 60);
	                
	                if (hoursDiff < 24) {
	                    alert('Cannot cancel booking within 24 hours of the event date.');
	                    return;
	                }
	            }
	            
	            // Confirm cancellation
	            if (!confirm('Are you sure you want to cancel this booking?')) {
	                return;
	            }
	            
	            try {
	                const response = await fetch(`/userdashboard/cancel-booking/${bookingId}`, {
	                    method: 'POST',
	                    headers: {
	                        'Content-Type': 'application/json'
	                    }
	                });
	                
	                if (response.ok) {
	                    const result = await response.json();
	                    if (result.success) {
	                        alert('Booking cancelled successfully!');
	                        location.reload(); // Reload to update the booking list
	                    } else {
	                        alert(result.message || 'Failed to cancel booking');
	                    }
	                } else {
	                    alert('Failed to cancel booking. Please try again.');
	                }
	            } catch (error) {
	                console.error('Error cancelling booking:', error);
	                alert('An error occurred while cancelling the booking.');
	            }
	        });
	    });
	})();
	// FEEDBACK MODULE
	(function setupFeedback() {
	    const modal = document.getElementById('feedbackModal');
	    const fbBookingId = document.getElementById('fbBookingId');
	    const fbServiceName = document.getElementById('fbServiceName');
	    const fbComment = document.getElementById('fbComment');
	    const starWrap = document.getElementById('starWrap');
	
	    let selectedRating = 5;
	    if (!modal) return;
	
	    // open modal when clicking "Give Feedback"
	    document.querySelectorAll('.give-feedback-btn').forEach(btn => {
	        btn.addEventListener('click', () => {
	            fbBookingId.value = btn.dataset.id;
	            fbServiceName.textContent = "Feedback — " + btn.dataset.service;
	            fbComment.value = "";
	            selectedRating = 5;
	            updateStarUI();
	            modal.classList.remove('hidden');
	            modal.style.display = 'flex';
	        });
	    });
	
	    // star selection
	    starWrap.querySelectorAll('.star-btn').forEach(b => {
	        b.addEventListener('click', () => {
	            selectedRating = parseInt(b.dataset.value, 10);
	            updateStarUI();
	        });
	    });
	
	    function updateStarUI() {
	        starWrap.querySelectorAll('.star-btn').forEach(b => {
	            const value = parseInt(b.dataset.value, 10);
	            if (value <= selectedRating) {
	                b.classList.add('bg-yellow-300');
	            } else {
	                b.classList.remove('bg-yellow-300');
	            }
	        });
	    }
	
	    // close feedback modal
	    document.getElementById('fbCancel').addEventListener('click', () => {
	        modal.classList.add('hidden');
	        modal.style.display = 'none';
	    });
	
	    // submit feedback
	    document.getElementById('fbSubmit').addEventListener('click', async () => {
	        const id = fbBookingId.value;
	        const comment = fbComment.value;
	
	        const payload = new URLSearchParams();
	        payload.append('bookingId', id);
	        payload.append('rating', selectedRating);
	        payload.append('comment', comment);
	
	        const res = await fetch('/userdashboard/feedback', {
	            method: 'POST',
	            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
	            body: payload.toString()
	        });
	
	        const data = await res.json();
	
	        if (res.ok && data.ok) {
	            const btn = document.querySelector(`.give-feedback-btn[data-id="${id}"]`);
	            if (btn) {
	                const td = btn.closest('td');
	                btn.remove();
	
	                const span = document.createElement('span');
	                span.className = 'text-sm text-gray-600 ms-2';
	                span.textContent = 'Feedback submitted';
	                td.appendChild(span);
	            }
	
	            modal.classList.add('hidden');
	            modal.style.display = 'none';
	        } else {
	            alert(data.error || "Could not submit feedback");
	        }
	    });
	})();
}

// --------------------------------------------------------------------
// PROVIDER – ADD SERVICES PAGE
// --------------------------------------------------------------------
function setupProviderDashboard() {
    const menuButtons = [...document.querySelectorAll('.service-menu-item')];
    const serviceTitle = document.getElementById('service-title');
    const addBtn = document.getElementById('add-service-btn');
    const formWrap = document.getElementById('service-form');
    const serviceListWrap = document.getElementById('service-list');
    const emptyState = document.getElementById('empty-state');
    const cancelBtn = document.getElementById('cancel-btn');
    const imageInput = document.getElementById('service-images');
    const imagePreview = document.getElementById('image-preview');

    if (!menuButtons.length) return;

    const pretty = key =>
        key.split('-').map(s => s.charAt(0).toUpperCase() + s.slice(1)).join(' ');

    function filterTo(key) {
        const cards = [...document.querySelectorAll('.service-card')];

        cards.forEach(card => {
            const cat = card.getAttribute('data-category') || '';
            card.classList.toggle('hidden', cat !== key);
        });

        serviceTitle.textContent = "Manage " + pretty(key);

        const visible = document.querySelectorAll('.service-card:not(.hidden)').length;
        if (visible > 0) {
            serviceListWrap.classList.remove('hidden');
            emptyState.classList.add('hidden');
        } else {
            serviceListWrap.classList.add('hidden');
            emptyState.classList.remove('hidden');
        }
    }

    menuButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            menuButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            const key = btn.dataset.service;
            const categoryInput = document.getElementById('service-category');

            if (categoryInput) categoryInput.value = pretty(key);

            filterTo(key);
        });
    });

    addBtn.addEventListener('click', () => {
        const active = document.querySelector('.service-menu-item.active');
        const key = active ? active.dataset.service : 'wedding-hall';

        const categoryInput = document.getElementById('service-category');
        if (categoryInput) categoryInput.value = pretty(key);

        const idField = document.getElementById('service-id');
        if (idField) idField.value = "";

        clearFormFields();

        formWrap.classList.remove('hidden');
        serviceListWrap.classList.add('hidden');
        emptyState.classList.add('hidden');
    });

    if (cancelBtn) {
        cancelBtn.addEventListener('click', () => {
            formWrap.classList.add('hidden');
            imagePreview.innerHTML = '';

            const count = parseInt(document.getElementById('serviceCount').value);
            if (count > 0) {
                serviceListWrap.classList.remove('hidden');
                emptyState.classList.add('hidden');
            } else {
                serviceListWrap.classList.add('hidden');
                emptyState.classList.remove('hidden');
            }
        });
    }

    if (imageInput && imagePreview) {
        imageInput.addEventListener('change', e => {
            imagePreview.innerHTML = '';
            [...e.target.files].forEach(file => {
                const reader = new FileReader();
                reader.onload = ev => {
                    const img = document.createElement('img');
                    img.src = ev.target.result;
                    img.className = 'w-24 h-24 object-cover rounded';
                    imagePreview.appendChild(img);
                };
                reader.readAsDataURL(file);
            });
        });
    }
}

// --------------------------------------------------------------------
// ON PAGE LOAD (EDIT MODE AUTO SELECT + FILTER)
// --------------------------------------------------------------------
function setupFormInitialState() {
    const idField = document.getElementById('service-id');
    const formWrap = document.getElementById('service-form');

    if (idField && idField.value) {
        formWrap.classList.remove('hidden');

        const catInput = document.getElementById('service-category');
        if (catInput && catInput.value) {
            const key = catInput.value.toLowerCase().replace(/\s+/g, '-');
            const btn = document.querySelector(`.service-menu-item[data-service="${key}"]`);

            if (btn) btn.click();
        }
    } else {
        const active = document.querySelector('.service-menu-item.active');
        if (active) active.click();
    }
}

// clear form fields
function clearFormFields() {
    const fields = [
        'service-name',
        'business-email',
        'business-phone',
        'location',
        'price',
        'address',
        'description'
    ];

    fields.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });

    const image = document.getElementById('service-images');
    if (image) image.value = '';

    const preview = document.getElementById('image-preview');
    if (preview) preview.innerHTML = '';
}

// helper
function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}
// --------------------------------------------------------------------
// CHECKSTATUS PAGE (Booking Approval + Current + Past)
// --------------------------------------------------------------------
function setupProviderCheckStatus() {

    const approvalTab = document.getElementById("tab-approval");
    const currentTab = document.getElementById("tab-current");
    const pastTab = document.getElementById("tab-past");

    const approvalSection = document.getElementById("approval-section");
    const currentSection = document.getElementById("current-section");
    const pastSection = document.getElementById("past-section");

    // ---------- Tab Switch Helper ----------
    function activateTab(activeBtn, activeSection) {
        // Reset all
        [approvalTab, currentTab, pastTab].forEach(btn => {
            btn.classList.remove("bg-indigo-600", "text-white");
            btn.classList.add("bg-gray-100", "text-gray-700");
        });

        [approvalSection, currentSection, pastSection].forEach(sec => {
            sec.classList.add("hidden");
        });

        // Activate chosen
        activeBtn.classList.add("bg-indigo-600", "text-white");
        activeBtn.classList.remove("bg-gray-100", "text-gray-700");

        activeSection.classList.remove("hidden");
    }

    // ---------- Tab Clicks ----------
    if (approvalTab) {
        approvalTab.addEventListener("click", () => {
            activateTab(approvalTab, approvalSection);
        });
    }

    if (currentTab) {
        currentTab.addEventListener("click", () => {
            activateTab(currentTab, currentSection);
        });
    }

    if (pastTab) {
        pastTab.addEventListener("click", () => {
            activateTab(pastTab, pastSection);
        });
    }

    // Default tab on load = Approval
    if (approvalTab && approvalSection) {
        activateTab(approvalTab, approvalSection);
    }

    // ---------- VIEW BOOKING DETAILS ----------
    document.querySelectorAll(".provider-view-btn").forEach(btn => {
        btn.addEventListener("click", async () => {
            const id = btn.dataset.id;
            if (!id) return;

            const res = await fetch(`/providerdashboard/booking-details/${id}`);
            if (!res.ok) return;

            const data = await res.json();

            // Set modal data
            document.getElementById("pService").textContent = data.serviceName || "";
            document.getElementById("pCategory").textContent = data.category || "";
            document.getElementById("pLocation").textContent = data.location || "";
            document.getElementById("pCustomer").textContent = data.customerName || "";
            document.getElementById("pEmail").textContent = data.customerEmail || "";
            document.getElementById("pPhone").textContent = data.customerPhone || "";
            document.getElementById("pDate").textContent = data.bookingDate || "";
            document.getElementById("pStatus").textContent = data.status || "";
            document.getElementById("pPayment").textContent = data.paymentStatus || "";

            // Feedback
            const fbWrap = document.getElementById("pFeedbackWrap");
            const pRating = document.getElementById("pRating");
            const pComment = document.getElementById("pComment");

            if (data.rating != null) {
                pRating.textContent = "Rating: " + data.rating + "/5";
                pComment.textContent = data.comment || "";
                fbWrap.style.display = "block";
            } else {
                pRating.textContent = "";
                pComment.textContent = "No feedback given";
                fbWrap.style.display = "block";
            }

            // Show modal
            const modal = document.getElementById("providerBookingModal");
            modal.classList.remove("hidden");
            modal.classList.add("flex");
        });
    });

    // ---------- APPROVAL ACTIONS (Approve / Reject) ----------
    document.querySelectorAll(".provider-action-btn").forEach(btn => {
        btn.addEventListener("click", async () => {
            const id = btn.dataset.id;
            const action = btn.dataset.action;

            if (!id || !action) return;

            const form = new URLSearchParams();
            form.append("action", action.trim());

            const res = await fetch(`/providerdashboard/booking/${id}/action`, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: form.toString()
            });

            if (!res.ok) {
                alert("Failed to update booking status");
                return;
            }

            // Reload to show updated sections
            location.reload();
        });
    });

    // ---------- CLOSE MODAL ----------
    const closeBtn = document.getElementById("closeProviderModal");
    if (closeBtn) {
        closeBtn.addEventListener("click", () => {
            const modal = document.getElementById("providerBookingModal");
            modal.classList.add("hidden");
        });
    }
}
// --------------------------------------------------------------------
// PROVIDER BILL PAGE
// --------------------------------------------------------------------
function setupProviderBill(){
	const tableBody = document.getElementById("bills-table-body");
    const emptyState = document.getElementById("empty-bills");

    const periodFilter = document.getElementById("period-filter");
    const serviceFilter = document.getElementById("service-filter");
    const statusFilter = document.getElementById("status-filter");

    if (!tableBody) return; // exit if page not present

    async function loadBills() {
        const res = await fetch(`/providerdashboard/bills/data`);
        if (!res.ok) return;

        const bills = await res.json();
        renderBills(bills);

        // attach filtering
        periodFilter.addEventListener("change", () => applyFilters(bills));
        serviceFilter.addEventListener("change", () => applyFilters(bills));
        statusFilter.addEventListener("change", () => applyFilters(bills));
    }

    function applyFilters(bills) {
        let filtered = [...bills];

        // status filter
        const s = statusFilter.value;
        if (s !== "all") {
            filtered = filtered.filter(b => b.status.toLowerCase() === s);
        }

        // service filter
        const sf = serviceFilter.value;
        if (sf !== "all") {
            filtered = filtered.filter(b => b.category.toLowerCase().includes(sf));
        }

        // period filter (simple version)
        const pf = periodFilter.value;
        if (pf === "monthly") {
            const month = new Date().getMonth() + 1;
            filtered = filtered.filter(b => new Date(b.createdDate).getMonth() + 1 === month);
        } else if (pf === "yearly") {
            const year = new Date().getFullYear();
            filtered = filtered.filter(b => new Date(b.createdDate).getFullYear() === year);
        }

        renderBills(filtered);
    }

    function renderBills(list) {
        tableBody.innerHTML = "";

        if (list.length === 0) {
            emptyState.classList.remove("hidden");
            return;
        }

        emptyState.classList.add("hidden");

        list.forEach(b => {
            const row = `
              <tr>
                <td class="px-4 py-3">${b.billId}</td>
                <td class="px-4 py-3">${b.serviceName}</td>
                <td class="px-4 py-3">${b.eventDate}</td>
                <td class="px-4 py-3">₹${b.amount}</td>
                <td class="px-4 py-3">
                    <span class="px-2 py-1 rounded ${
                        b.status === 'Paid'
                        ? 'bg-green-100 text-green-700'
                        : 'bg-yellow-100 text-yellow-700'
                    }">
                    ${b.status}
                    </span>
                </td>
                <td class="px-4 py-3">
                    <button class="px-3 py-1 bg-indigo-600 text-white rounded">Download</button>
                </td>
              </tr>
            `;
            tableBody.insertAdjacentHTML("beforeend", row);
        });
    }

    loadBills();
}
