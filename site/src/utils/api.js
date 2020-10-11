
const BASE_HOST = "http://localhost:8000";

const fetchGet = (url) => fetch(BASE_HOST + url);
const fetchPost = (url, data) => {
    return fetch(BASE_HOST + url, {
        method: "POST",
        header: { "Content-type": "application/json"},
        body: JSON.stringify(data)
    });
}

const API = {
    fetchAllUsers: () => fetchGet("/users/all"),
    fetchUpsertUser: (data) => fetchPost("/users/upsert", data),
    fetchGetUserStat: (userId) => fetchGet("/users/stat?userId=" + userId),
    fetchAllMatches: () => fetchGet("/matches/all"),
    fetchEndMatch : (matchId) => fetchPost("/matches/end?matchId=" + matchId)
}

export default API;