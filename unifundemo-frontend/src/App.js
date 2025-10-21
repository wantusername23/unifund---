import React, { useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, Link } from 'react-router-dom';
import Layout from './components/layout/Layout';
import Login from './components/auth/Login';
import SignUp from './components/auth/SignUp';
import WorldviewList from './components/worldview/WorldviewList';
import WorldviewDetail from './components/worldview/WorldviewDetail';
import CreateWorldview from './components/worldview/CreateWorldview';
import Profile from './components/profile/Profile';
import PostDetail from './components/post/PostDetail';
import BookmarkList from './components/bookmark/BookmarkList';
import NotificationList from './components/notification/NotificationList';
import CreatePost from './components/post/CreatePost';
import CreatorDashboard from './components/worldview/CreatorDashboard'; // ✅ 추가
import PopularPosts from './components/board/PopularPosts'; // ✅ 추가
import { subscribeToNotifications } from './api';

function App() {
    const token = localStorage.getItem('token');

    useEffect(() => {
        if (token) {
            try {
                const sse = subscribeToNotifications();
                sse.addEventListener('notification', (event) => {
                    const data = JSON.parse(event.data);
                    alert(`New Notification: ${data.message}`);
                });
                sse.onerror = () => {
                    sse.close();
                }
                return () => {
                    sse.close();
                };
            } catch (error) {
                console.error("SSE connection error:", error);
            }
        }
    }, [token]);

    return (
        <Router>
            <Layout>
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/signup" element={<SignUp />} />
                    <Route path="/profile" element={<Profile />} />
                    <Route path="/bookmarks" element={<BookmarkList />} />
                    <Route path="/notifications" element={<NotificationList />} />
                    <Route path="/create-worldview" element={<CreateWorldview />} />
                    <Route path="/popular" element={<PopularPosts />} /> {/* ✅ 추가 */}
                    <Route path="/worldviews/:worldviewId/create-post" element={<CreatePost />} />
                    <Route path="/worldviews/:id/admin/*" element={<CreatorDashboard />} /> {/* ✅ 추가 */}
                    <Route path="/worldviews/:id/*" element={<WorldviewDetail />} />
                    <Route path="/worldviews/:worldviewId/posts/:postId" element={<PostDetail />} />
                    <Route path="/" element={<WorldviewList />} />
                </Routes>
            </Layout>
        </Router>
    );
}

export default App;