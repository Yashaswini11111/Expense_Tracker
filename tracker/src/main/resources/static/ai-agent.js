(function() {
    const activeUser = JSON.parse(localStorage.getItem("user"));
    if (!activeUser) return; 

    // 1. Inject Premium Styles
    const aiStyles = `
        #ai-sidebar {
            position: fixed; top: 0; right: 0; width: 380px; height: 100vh;
            background: #ffffff; box-shadow: -10px 0 40px rgba(0,0,0,0.1);
            display: flex; flex-direction: column; z-index: 9999;
            transition: transform 0.5s cubic-bezier(0.16, 1, 0.3, 1);
            font-family: 'Poppins', sans-serif; transform: translateX(100%);
        }
        #ai-sidebar.open { transform: translateX(0); }
        #ai-toggle-btn {
            position: fixed; bottom: 30px; right: 30px; background: #264653; color: white;
            padding: 15px 25px; border-radius: 50px; cursor: pointer; display: flex;
            align-items: center; gap: 10px; box-shadow: 0 8px 20px rgba(0,0,0,0.2);
            z-index: 9998; transition: 0.3s; border: 2px solid #E9C46A; font-family: 'Poppins', sans-serif;
        }
        #ai-toggle-btn:hover { background: #E9C46A; color: #264653; transform: scale(1.05); }
        .ai-header { background: linear-gradient(135deg, #264653 0%, #2a9d8f 100%); color: white; padding: 25px 20px; display: flex; justify-content: space-between; align-items: center; }
        #ai-chat-body { flex: 1; padding: 20px; overflow-y: auto; display: flex; flex-direction: column; gap: 15px; background: #fdfcf8; border-bottom: 1px solid #eee; }
        .ai-bubble, .user-bubble { max-width: 85%; padding: 12px 16px; border-radius: 18px; font-size: 13.5px; line-height: 1.6; }
        .ai-bubble { background: white; border: 1px solid #eef2f2; color: #333; border-bottom-left-radius: 4px; box-shadow: 0 4px 12px rgba(0,0,0,0.03); }
        .user-bubble { background: #E9C46A; color: #264653; align-self: flex-end; border-bottom-right-radius: 4px; font-weight: 600; }
        #ai-typing { color: #2a9d8f; font-style: italic; font-size: 12px; padding: 0 20px; display: none; margin-bottom: 10px; }
        .ai-footer { padding: 20px; background: white; }
        .ai-input-group { display: flex; gap: 10px; background: #f4f7f6; padding: 8px 18px; border-radius: 30px; align-items: center; }
        .ai-input-group input { border: none !important; background: none !important; flex: 1 !important; outline: none !important; padding: 5px 0 !important; font-size: 13px !important; margin: 0 !important; width: 100% !important;}
        .ai-input-group button { background: none; border: none; color: #264653; cursor: pointer; font-size: 18px; }
    `;
    const styleSheet = document.createElement("style");
    styleSheet.innerText = aiStyles;
    document.head.appendChild(styleSheet);

    // 2. Inject HTML
    const aiHTML = `
        <div id="ai-sidebar">
            <div class="ai-header">
                <h4 style="margin:0"><i class="fas fa-robot"></i> AI Budget Mentor</h4>
                <button onclick="document.getElementById('ai-sidebar').classList.remove('open')" style="background:none; border:none; color:white; cursor:pointer; font-size:20px;">&times;</button>
            </div>
            <div id="ai-chat-body"></div>
            <div id="ai-typing"><i class="fas fa-circle-notch fa-spin"></i> Mentoring in progress...</div>
            <div class="ai-footer">
                <div class="ai-input-group">
                    <input type="text" id="ai-query" placeholder="Ask me about trends or tech..." onkeypress="if(event.key==='Enter') window.processAI()">
                    <button onclick="window.processAI()"><i class="fas fa-paper-plane"></i></button>
                </div>
            </div>
        </div>
        <div id="ai-toggle-btn" onclick="document.getElementById('ai-sidebar').classList.add('open')">
            <i class="fas fa-robot"></i> <span>Ask Mentor</span>
        </div>
    `;
    document.body.insertAdjacentHTML('beforeend', aiHTML);

    const chatBody = document.getElementById('ai-chat-body');
    
    window.appendMsg = (content, sender) => {
        const div = document.createElement('div');
        div.className = sender === 'ai' ? 'ai-bubble' : 'user-bubble';
        div.innerHTML = content;
        chatBody.appendChild(div);
        chatBody.scrollTop = chatBody.scrollHeight;
    };

    window.processAI = async () => {
        const input = document.getElementById('ai-query');
        const query = input.value.trim().toLowerCase();
        if (!query) return;

        appendMsg(input.value, 'user');
        input.value = "";
        document.getElementById('ai-typing').style.display = "block";

        setTimeout(() => {
            document.getElementById('ai-typing').style.display = "none";
            let res = "I'm here for you, buddy! I'm specifically trained on your **BudgetWise** application logic. Try asking about 'Visual Trends', 'Budget Analysis', or 'How we built this'.";

            // --- DEEP KNOWLEDGE BASE ---

            // 1. VISUAL TRENDS
            if (query.includes("visual trends") || query.includes("trend") || query.includes("graph") || query.includes("chart")) {
                res = `The **Visual Trends** module is where we turn your raw data into insights! 📊<br><br>` +
                      `• It uses **Chart.js** to fetch your last 6 months of data from MySQL.<br>` +
                      `• We use a **Line Chart** to show your spending flow over time.<br>` +
                      `• It helps you identify which months you spend the most so you can save better next time.`;
            }
            // 2. BUDGETING / ANALYSIS
            else if (query.includes("budget") || query.includes("limit") || query.includes("analysis")) {
                res = `The **Budget Analysis** page is your discipline center. 🎯<br><br>` +
                      `• You set a monthly limit for categories like Food or Rent.<br>` +
                      `• I use a **Bar Chart** to compare your **Real Spending** vs **Your Goal**.<br>` +
                      `• This is real-time logic—every time you add an expense, the bar updates instantly!`;
            }
            // 3. TECH STACK (VIVA PREP)
            else if (query.includes("tech") || query.includes("built") || query.includes("code") || query.includes("how it works")) {
                res = `Great technical question! Here is how we built this: 💻<br><br>` +
                      `• **Backend:** Java Spring Boot 3.4 (MVC Architecture).<br>` +
                      `• **Database:** Oracle MySQL stored locally.<br>` +
                      `• **Data Layer:** Spring Data JPA with custom JPQL queries.<br>` +
                      `• **Security:** Session management using Browser LocalStorage.`;
            }
            // 4. NOTIFICATIONS / ALERTS
            else if (query.includes("notification") || query.includes("alert") || query.includes("salary")) {
                const salary = activeUser.monthlyIncome || 0;
                res = `My **AI Advisor logic** is always watching your back. 🛑<br><br>` +
                      `• I compare your total expenses to your salary (₹${salary}).<br>` +
                      `• If expenses cross that limit, I trigger a **Red Bell Notification**.<br>` +
                      `• It's designed to keep you from going into debt!`;
            }
            // 5. PROFILE / PIC / DOB
            else if (query.includes("profile") || query.includes("picture") || query.includes("dob") || query.includes("photo")) {
                res = `You can personalize your experience in the **Profile** section.<br><br>` +
                      `• Upload a photo to see it in your header (stored as **Base64**).<br>` +
                      `• Update your **DOB** and **Monthly Salary** so I can calculate your budget health accurately!`;
            }
            // 6. EXPORT
            else if (query.includes("export") || query.includes("pdf") || query.includes("download")) {
                res = `Need a physical report? Go to the **Community** page.<br><br>` +
                      `• You can download a styled **PDF Statement** of all your transactions.<br>` +
                      `• I use the **OpenPDF** library to generate these professional reports on the fly.`;
            }
            // 7. GREETINGS
            else if (query.includes("hi") || query.includes("hello") || query.includes("hey")) {
                res = `Hey **${activeUser.fullName}**! 👋 I'm your financial buddy. I've been watching your dashboard updates. How can I help you manage your money today?`;
            }

            appendMsg(res, 'ai');
        }, 1000);
    };

    // Initial Greeting
    setTimeout(() => {
        appendMsg(`Hi **${activeUser.fullName}**! I'm your AI Mentor. I know every inch of this project. Ask me about your **Trends**, **Budgets**, or the **Tech Stack** we used!`, 'ai');
    }, 1200);

})();