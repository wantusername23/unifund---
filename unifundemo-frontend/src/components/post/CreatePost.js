import React, { useState } from 'react';
import { createPost } from '../../api';
import { useParams, useNavigate } from 'react-router-dom';

const CreatePost = () => {
    const { worldviewId } = useParams();
    const navigate = useNavigate();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [tags, setTags] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const tagSet = tags.split(',').map(tag => tag.trim()).filter(Boolean);
            await createPost(worldviewId, 'free', { // 'free' 게시판으로 고정
                title,
                content,
                tags: tagSet
            });
            navigate(`/worldviews/${worldviewId}/posts`);
        } catch (err) {
            setError('Failed to create post. Are you a member?');
            console.error(err);
        }
    };

    return (
        <div className="max-w-2xl mx-auto bg-white p-8 rounded-lg shadow-md">
            <h2 className="text-3xl font-bold text-gray-800 mb-6">Write a New Post</h2>
            {error && <p className="text-red-500 text-sm mb-4">{error}</p>}
            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700">Title</label>
                    <input
                        type="text"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        className="w-full px-4 py-2 mt-1 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        required
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Content</label>
                    <textarea
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                        className="w-full px-4 py-2 mt-1 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        rows="10"
                        required
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Tags (comma-separated)</label>
                    <input
                        type="text"
                        value={tags}
                        onChange={(e) => setTags(e.target.value)}
                        className="w-full px-4 py-2 mt-1 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                <button type="submit" className="w-full py-2 px-4 font-semibold text-white bg-blue-500 rounded-md hover:bg-blue-600 transition duration-300">
                    Submit Post
                </button>
            </form>
        </div>
    );
};

export default CreatePost;