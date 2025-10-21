import React, { useState, useEffect } from 'react';
import { getPopularPosts } from '../../api';
import { Link } from 'react-router-dom';

const PopularPosts = () => {
    const [posts, setPosts] = useState([]);

    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const { data } = await getPopularPosts();
                setPosts(data);
            } catch (error) {
                console.error("Failed to fetch popular posts", error);
            }
        };
        fetchPosts();
    }, []);

    return (
        <div className="max-w-3xl mx-auto bg-white p-8 rounded-lg shadow-md">
            <h2 className="text-3xl font-bold text-gray-800 mb-6">Popular Posts</h2>
            <div className="space-y-4">
                {posts.length > 0 ? (
                    posts.map(post => (
                        <div key={post.id} className="p-4 border rounded-md hover:bg-gray-50">
                            <Link to={`/worldviews/0/posts/${post.id}`} className="text-lg font-semibold text-blue-600 hover:underline">
                                {post.title}
                            </Link>
                            <p className="text-sm text-gray-500">by {post.authorNickname} | Recommendations: {post.recommendations}</p>
                        </div>
                    ))
                ) : (
                    <p>No popular posts right now.</p>
                )}
            </div>
        </div>
    );
};

export default PopularPosts;