const API = "/api/v1/tasks";

let page = 0, size = 10;

const els = {
    rows: qs("#rows"),
    count: qs("#countBadge"),
    pageInfo: qs("#pageInfo"),
    prev: qs("#prev"),
    next: qs("#next"),
    denseToggle: qs("#denseToggle"),
    empty: qs("#empty"),
    tableWrap: qs("#tableWrap"),
    spinner: qs("#spinner"),
    toast: qs("#toast"),
    openCreate: qs("#openCreate"),
    createForm: qs("#createForm"),
    filtersForm: qs("#filtersForm"),
    clearFilters: qs("#clearFilters"),
    submitCreate: qs("#submitCreate"),
    searchBtn: qs("#searchBtn"),
    filterBtn: qs("#filterBtn"),
    filterModal: qs("#filterModal"),
    closeFilter: qs("#closeFilter"),
    // Sort Controls
    sortSelect: qs("#sortSelect"),
};

function qs(sel){ return document.querySelector(sel); }
function val(id){ return qs("#"+id).value?.trim(); }
function iso(dt){ return dt ? new Date(dt).toISOString() : null; }
function html(s){ return (s??"").replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#039;'}[c])); }
function show(el, yes){ el.classList.toggle("hidden", !yes); }
function toast(msg){ els.toast.textContent = msg; show(els.toast,true); setTimeout(()=>show(els.toast,false),2000); }
function spin(yes){ show(els.spinner, yes); }


async function apiList(){
    const sortValue = els.sortSelect ? els.sortSelect.value : qs("#fSort").value;
    const p = new URLSearchParams({ page, size, sort: sortValue });
    const fAssignee = val("fAssignee");
    const fCategory = val("fCategory");
    const fStatus = qs("#fStatus").value || "";
    const fDueAfter  = val("fDueAfter");
    const fDueBefore = val("fDueBefore");
    if (fAssignee) p.set("assignee", fAssignee);
    if (fCategory) p.set("category", fCategory);
    if (fStatus) p.set("status", fStatus);
    if (fDueAfter) p.set("dueAfter", new Date(fDueAfter+"T00:00:00").toISOString());
    if (fDueBefore) p.set("dueBefore", new Date(fDueBefore+"T23:59:59").toISOString());
    return fetch(`${API}?${p.toString()}`).then(r => ok(r));
}

async function ok(r){
    if (r.ok) return r.json();
    let msg = `${r.status} ${r.statusText}`;
    try {
        const ct = r.headers.get("content-type") || "";
        if (ct.includes("application/json")) {
            const j = await r.json();
            msg = j.message || msg;
            if (j.fieldErrors?.length) {
                msg += " • " + j.fieldErrors.map(fe => `${fe.field}: ${fe.message}`).join(", ");
            }
        } else {
            msg = await r.text();
        }
    } catch { /* ignore parse errors */ }
    throw new Error(msg);
}

function render(pageData){
    const content = pageFilter(pageData.content);
    els.rows.innerHTML = "";
    content.forEach(t=>{
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>
        ${t.assignee ?? ""}
      </td>
      <td>
        <div class="task-description">
          <div class="title">${html(t.title)}</div>
          ${t.description ? `<div class="muted">${html(t.description)}</div>` : ""}
        </div>
      </td>
      <td>${t.category ?? ""}</td>
      <td>
        <span class="priority-badge ${t.priority?.toLowerCase() || 'medium'}">
          ${t.priority || 'MEDIUM'}
        </span>
      </td>
      <td>
        ${statusPill(t.status)}
      </td>
      <td>${t.dueDate ? new Date(t.dueDate).toLocaleDateString() : ""}</td>
      <td>
        <button class="btn icon" data-act="done" data-id="${t.id}" ${t.status==="DONE"?"disabled":""}>✅ Done</button>
        <button class="btn ghost" data-act="del" data-id="${t.id}">🗑️ Delete</button>
      </td>`;
        els.rows.appendChild(tr);
    });

    els.count.textContent = `${pageData.totalElements} task${pageData.totalElements===1?"":"s"}`;
    els.pageInfo.textContent = `Page ${pageData.page+1} / ${Math.max(pageData.totalPages,1)}`;
    els.prev.disabled = pageData.page<=0;
    els.next.disabled = pageData.page+1 >= Math.max(pageData.totalPages,1);

    show(els.empty, content.length===0);
}

function pageFilter(items){
    const q = val("fText")?.toLowerCase();
    if(!q) return items;
    return items.filter(t =>
        (t.title ?? "").toLowerCase().includes(q) ||
        (t.description ?? "").toLowerCase().includes(q)
    );
}

function pill(status){
    const cls = status==="DONE" ? "done" : status==="IN_PROGRESS" ? "inprog" : "open";
    return `<span class="pill ${cls}">${status}</span>`;
}

function statusPill(status){
    let cls, displayText;
    switch(status) {
        case "DONE":
            cls = "done";
            displayText = "✅ Completed";
            break;
        case "IN_PROGRESS":
            cls = "inprog";
            displayText = "🔄 In Progress";
            break;
        case "OPEN":
        default:
            cls = "open";
            displayText = "⏰ Pending";
            break;
    }
    return `<span class="pill ${cls}">${displayText}</span>`;
}

async function load(){
    try{
        spin(true);
        const data = await apiList();
        render(data);
    }catch(e){
        toast(`Failed to load: ${e.message}`);
        console.error(e);
    }finally{
        spin(false);
    }
}

async function createTask(e){
    e?.preventDefault(); // prevent form submit
    const title = val("title");
    if (!title) { toast("Title is required"); return; }

    const body = {
        title,
        description: val("description") || null,
        assignee: val("assignee") || null,
        category: val("category") || null,
        priority: qs("#priority").value || null,
        dueDate: iso(val("dueDate"))
    };
    try{
        spin(true);
        await fetch(API, { method:"POST", headers:{ "Content-Type":"application/json" }, body: JSON.stringify(body) })
            .then(r=>ok(r));
        els.createForm.reset();
        toast("✨ Task created successfully!");
        page = 0;
        load();
    }catch(e){
        toast(`Create failed: ${e.message}`);
        console.error(e);
    }finally{
        spin(false);
    }
}

async function markDone(id){
    console.log("Marking task as done:", id);
    try{
        const response = await fetch(`${API}/${id}`, { 
            method:"PATCH", 
            headers:{ "Content-Type":"application/json" }, 
            body: JSON.stringify({ status:"DONE" }) 
        });
        
        console.log("Response status:", response.status);
        
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        await response.json();
        toast("✅ Task marked as done!");
        load();
    }catch(e){ 
        console.error("Error marking task as done:", e);
        toast(`❌ Update failed: ${e.message}`); 
    }
}

async function removeTask(id){
    try{
        const r = await fetch(`${API}/${id}`, { method:"DELETE" });
        if(!r.ok && r.status!==204) throw new Error("delete failed");
        toast("Deleted");
        load();
    }catch(e){ toast(`Delete failed: ${e.message}`); console.error(e); }
}

function wire(){
    els.prev.addEventListener("click", ()=>{ if(page>0){ page--; load(); }});
    els.next.addEventListener("click", ()=>{ page++; load(); });
    els.rows.addEventListener("click", (ev)=>{
        console.log("Table row clicked:", ev.target);
        const btn = ev.target.closest("button[data-act]");
        if(!btn) {
            console.log("No button with data-act found");
            return;
        }
        const id = btn.dataset.id;
        const act = btn.dataset.act;
        console.log("Button clicked:", act, "ID:", id);
        if(act==="done") markDone(id);
        if(act==="del")  removeTask(id);
    });

    // create task form
    if (els.submitCreate) els.submitCreate.addEventListener("click", createTask);
    els.createForm.addEventListener("submit", (e)=> e.preventDefault());

    // search functionality
    els.searchBtn.addEventListener("click", ()=>{ page=0; load(); });
    qs("#fText").addEventListener("keypress", (e)=>{ 
        if(e.key === "Enter") { page=0; load(); }
    });

    // sort functionality
    if (els.sortSelect) {
        els.sortSelect.addEventListener("change", ()=>{ page=0; load(); });
    }

    // filter modal
    els.filterBtn.addEventListener("click", ()=> show(els.filterModal, true));
    els.closeFilter.addEventListener("click", ()=> show(els.filterModal, false));

    // filters
    els.filtersForm.addEventListener("submit", (e)=>{ e.preventDefault(); page=0; load(); show(els.filterModal, false); });
    els.clearFilters.addEventListener("click", ()=>{ els.filtersForm.reset(); page=0; load(); });

    // compact table toggle
    els.denseToggle.addEventListener("change", (e)=>{
        document.body.classList.toggle("dense", e.target.checked);
    });

    // close modal on backdrop click
    els.filterModal.addEventListener("click", (e)=>{
        if(e.target === els.filterModal) show(els.filterModal, false);
    });

    // Debug: Add a test button to verify JavaScript is working
    console.log("JavaScript loaded and event listeners attached");
    console.log("Available elements:", Object.keys(els));
}

// Mark task as done
async function markDone(id) {
    console.log("Marking task as done:", id);
    try {
        const response = await fetch(`${API}/${id}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: 'DONE', completedAt: new Date().toISOString() })
        });
        
        if (!response.ok) {
            console.log("Response status:", response.status);
            const errorText = await response.text();
            console.log("Error response:", errorText);
            
            if (response.status === 500 && errorText.includes("Task not found")) {
                toast("⚠️ Task not found - refreshing list...");
                load(); // Refresh the list to remove stale data
                return;
            }
            
            throw new Error(`HTTP ${response.status}: ${errorText}`);
        }
        
        toast("✅ Task marked as completed!");
        load(); // Reload the list and update status dashboard
    } catch (error) {
        console.error("Error marking task as done:", error);
        toast(`Error marking task as done: ${error.message}`);
    }
}

// Remove task
async function removeTask(id) {
    if (!confirm("Are you sure you want to delete this task?")) return;
    
    try {
        const response = await fetch(`${API}/${id}`, { method: 'DELETE' });
        if (!response.ok) {
            const errorText = await response.text();
            console.log("Delete error response:", errorText);
            
            if (response.status === 500 && errorText.includes("Task not found")) {
                toast("⚠️ Task not found - refreshing list...");
                load(); // Refresh the list to remove stale data
                return;
            }
            
            throw new Error(`HTTP ${response.status}: ${errorText}`);
        }
        
        toast("🗑️ Task deleted successfully!");
        load(); // Reload the list and update status dashboard
    } catch (error) {
        console.error("Error deleting task:", error);
        toast(`Error deleting task: ${error.message}`);
    }
}

// Force refresh function to clear any stale data
function forceRefresh() {
    console.log("Force refreshing application...");
    els.rows.innerHTML = "";
    els.count.textContent = "0 tasks";
    els.pageInfo.textContent = "Page 1 / 1";
    page = 0;
    load();
}

// Add force refresh button (for debugging)
document.addEventListener('DOMContentLoaded', function() {
    // Add a refresh button to the header for debugging
    const refreshBtn = document.createElement('button');
    refreshBtn.textContent = '🔄 Refresh';
    refreshBtn.className = 'btn';
    refreshBtn.onclick = forceRefresh;
    document.querySelector('.actions').appendChild(refreshBtn);
});

wire();
load();
