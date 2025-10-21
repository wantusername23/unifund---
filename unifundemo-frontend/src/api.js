import axios from 'axios';

const apiClient = axios.create({
    baseURL: 'http://localhost:8080',
});

apiClient.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// --- User & Profile ---
export const login = (email, password) => apiClient.post('/api/users/login', { email, password });
export const signUp = (email, password, nickname) => apiClient.post('/api/users/signup', { email, password, nickname });
export const getProfile = () => apiClient.get('/api/profile');
export const updateProfile = (profileData) => apiClient.put('/api/profile', profileData);

// --- Worldview ---
export const getWorldviews = () => apiClient.get('/api/worldviews');
export const getWorldview = (id) => apiClient.get(`/api/worldviews/${id}`);
export const createWorldviewWithUpload = (formData) => apiClient.post('/api/worldviews/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
});
export const createWorldviewWithUrl = (data) => apiClient.post('/api/worldviews/url', data);
export const searchWorldviews = (query) => apiClient.get('/api/worldviews/search', { params: { q: query } });
export const searchWorldviewsByTag = (tag) => apiClient.get('/api/worldviews/search/by-tag', { params: { tag } });

// --- Post ---
export const getPosts = (worldviewId, boardType) => apiClient.get(`/api/worldviews/${worldviewId}/posts`, { params: { boardType } });
export const getPost = (worldviewId, postId) => apiClient.get(`/api/worldviews/${worldviewId}/posts/${postId}`);
export const createPost = (worldviewId, boardType, data) => apiClient.post(`/api/worldviews/${worldviewId}/posts/${boardType}`, data);
export const recommendPost = (worldviewId, postId) => apiClient.post(`/api/worldviews/${worldviewId}/posts/${postId}/recommend`);
export const getPopularPosts = () => apiClient.get('/api/boards/popular');
export const getPendingPosts = (worldviewId) => apiClient.get(`/api/worldviews/${worldviewId}/posts/pending`);
export const approvePost = (worldviewId, postId) => apiClient.patch(`/api/worldviews/${worldviewId}/posts/${postId}/approve`);
export const searchPostsByTag = (worldviewId, tag) => apiClient.get(`/api/worldviews/${worldviewId}/posts/search/by-tag`, { params: { tag } });


// --- Comment ---
export const getComments = (postId) => apiClient.get(`/api/posts/${postId}/comments`);
export const createComment = (postId, data) => apiClient.post(`/api/posts/${postId}/comments`, data);
export const recommendComment = (postId, commentId) => apiClient.post(`/api/posts/${postId}/comments/${commentId}/recommend`);

// --- Character (Worldview Sub) ---
export const getCharacters = (worldviewId) => apiClient.get(`/api/worldviews/${worldviewId}/characters`);
export const createCharacter = (worldviewId, data) => apiClient.post(`/api/worldviews/${worldviewId}/characters`, data);

// --- Timeline (Worldview Sub) ---
export const getTimelineEvents = (worldviewId) => apiClient.get(`/api/worldviews/${worldviewId}/timeline`);
export const createTimelineEvent = (worldviewId, data) => apiClient.post(`/api/worldviews/${worldviewId}/timeline`, data);

// --- Vote (Worldview Sub) ---
export const getVotes = (worldviewId) => apiClient.get(`/api/worldviews/${worldviewId}/votes`);
export const createVote = (worldviewId, data) => apiClient.post(`/api/worldviews/${worldviewId}/votes`, data);
export const castVote = (worldviewId, voteId, optionId) => apiClient.post(`/api/worldviews/${worldviewId}/votes/${voteId}/options/${optionId}`);

// --- Bookmark ---
export const getBookmarks = () => apiClient.get('/api/bookmarks');
export const addBookmark = (data) => apiClient.post('/api/bookmarks', data);
export const removeBookmark = (data) => apiClient.delete('/api/bookmarks', { data });

// --- Notification ---
export const getNotifications = () => apiClient.get('/api/notifications');
export const markNotificationAsRead = (id) => apiClient.patch(`/api/notifications/${id}/read`);
export const subscribeToNotifications = () => {
    const token = localStorage.getItem('token');
    if (!token) {
        throw new Error("No token found");
    }
    // Assuming backend supports token via query param for EventSource
    return new EventSource(`http://localhost:8080/api/notifications/subscribe?token=${token}`);
};

// --- Admin/Report ---
export const createReport = (data) => apiClient.post('/api/admin/reports', data);
// ✅ Added missing admin exports
export const getAdminWorldview = (id) => apiClient.get(`/api/admin/worldviews/${id}`);
export const getDistributionHistory = (id) => apiClient.get(`/api/admin/worldviews/${id}/history`);
export const distributeRevenue = (id) => apiClient.post(`/api/admin/worldviews/${id}/distribute`);

// --- Contributor ---
// ✅ Added missing contributor exports
export const addContributor = (worldviewId, data) => apiClient.post(`/api/worldviews/${worldviewId}/contributors`, data);
export const getContributors = (worldviewId) => apiClient.get(`/api/worldviews/${worldviewId}/contributors`);

// --- Membership ---
// ✅ Added missing membership exports
export const getMembershipTiers = (worldviewId) => apiClient.get(`/api/worldviews/${worldviewId}/memberships`);
export const addMembershipTier = (worldviewId, data) => apiClient.post(`/api/worldviews/${worldviewId}/memberships`, data);
export const subscribeToMembership = (membershipId) => apiClient.post(`/api/worldviews/memberships/${membershipId}/subscribe`);

// --- Tag ---
// ✅ Added missing tag export
export const getAllTags = () => apiClient.get('/api/tags');
export const searchInWorldview = (worldviewId, query) =>
    apiClient.get(`/api/worldviews/${worldviewId}/search`, { params: { q: query } });

export default apiClient;