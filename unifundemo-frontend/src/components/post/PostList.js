import React, { useState, useEffect } from 'react';
import { getPosts, getPendingPosts, approvePost } from '../../api';
import { Link } from 'react-router-dom';

const PostItem = ({ post, worldviewId }) => (
    <div className="p-4 border rounded-md hover:bg-gray-50">
        <Link to={`/worldviews/${worldviewId}/posts/${post.id}`} className="text-lg font-semibold text-blue-600 hover:underline">
            {post.title} {post.isNotice && <span className="text-red-500 text-sm ml-2">[Notice]</span>}
        </Link>
        <p className="text-sm text-gray-500">by {post.authorNickname}</p>
    </div>
);

const PostList = ({ worldviewId, isCreator }) => {
    const [posts, setPosts] = useState([]);
    const [pendingPosts, setPendingPosts] = useState([]);
    const [boardType, setBoardType] = useState('FREE'); // FREE, WORKS, PENDING

    const fetchPosts = async () => {
        try {
            const type = boardType === 'PENDING' ? 'WORKS' : boardType;
            const { data } = await getPosts(worldviewId, type);
            setPosts(data);
        } catch (error) {
            console.error("Failed to fetch posts", error);
        }
    };

    const fetchPendingPosts = async () => {
        if (!isCreator) return;
        try {
            const { data } = await getPendingPosts(worldviewId);
            setPendingPosts(data);
        } catch (error) {
            console.error("Failed to fetch pending posts", error);
        }
    };

    useEffect(() => {
        if (boardType === 'PENDING') {
            fetchPendingPosts();
        } else {
            fetchPosts();
        }
    }, [worldviewId, boardType, isCreator]);

    const handleApprove = async (postId) => {
        try {
            await approvePost(worldviewId, postId);
            fetchPendingPosts(); // 목록 새로고침
        } catch (error) {
            alert('Failed to approve post');
        }
    };

    return (
        <div className="bg-white p-8 rounded-lg shadow-md mt-6">
            <div className="flex justify-between items-center mb-4">
                <div className="flex space-x-4 border-b">
                    <button onClick={() => setBoardType('FREE')} className={`py-2 px-4 ${boardType === 'FREE' ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-500'}`}>Free Board</button>
                    <button onClick={() => setBoardType('WORKS')} className={`py-2 px-4 ${boardType === 'WORKS' ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-500'}`}>Works</button>
                    {isCreator && (
                        <button onClick={() => setBoardType('PENDING')} className={`py-2 px-4 ${boardType === 'PENDING' ? 'border-b-2 border-red-500 text-red-500' : 'text-gray-500'}`}>Pending ({pendingPosts.length})</button>
                    )}
                </div>
                <Link
                    to={`/worldviews/${worldviewId}/create-post`}
                    className="px-4 py-2 font-semibold text-white bg-green-500 rounded-md hover:bg-green-600 transition duration-300"
                >
                    Write Post
                </Link>
            </div>

            <div className="space-y-4">
                {boardType === 'PENDING' ? (
                    pendingPosts.length > 0 ? (
                        pendingPosts.map(post => (
                            <div key={post.id} className="p-4 border rounded-md flex justify-between items-center">
                                <div>
                                    <Link to={`/worldviews/${worldviewId}/posts/${post.id}`} className="text-lg font-semibold text-blue-600 hover:underline">{post.title}</Link>
                                    <p className="text-sm text-gray-500">by {post.authorNickname}</p>
                                </div>
                                <button onClick={() => handleApprove(post.id)} className="px-3 py-1 bg-green-500 text-white rounded-md hover:bg-green-600">Approve</button>
                            </div>
                        ))
                    ) : <p>No pending posts.</p>
                ) : (
                    posts.length > 0 ? (
                        posts.map(post => <PostItem key={post.id} post={post} worldviewId={worldviewId} />)
                    ) : <p>No posts in this board yet.</p>
                )}
            </div>
        </div>
    );
};

export default PostList;