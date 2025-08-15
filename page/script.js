async function saveDataName(name) {
    try {
        const response = await fetch(`/app/postInfo?name=${encodeURIComponent(name)}`, {
            method: "GET"
        });

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }
    } catch (error) {
        console.error("There was a problem with the fetch operation:", error);
    }
}

async function getData() {
    try {
        const response = await fetch("/app/getInfo", {
            method: "GET"
        });

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const data = await response.json();
        return data;

    } catch (error) {
        console.error("There was a problem with the fetch operation:", error);
    }
}

document.getElementById("post-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    await saveDataName(document.getElementById("post-name").value);
});

document.getElementById("get-form").addEventListener("submit", async (e) => {
    e.preventDefault();
    const data = await getData();
    document.getElementById("get-response-name").textContent = data.name || "No data found";
});
