import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom'; // ✅ Added Link import
import { getPost, getComments, createComment, recommendPost, createReport, addBookmark } from '../../api';

const PostDetail = () => {
    const { worldviewId, postId } = useParams();
    const [post, setPost] = useState(null);
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState('');
    // ✅ Defined missing state variables for report form
    const [showReport, setShowReport] = useState(false);
    const [reportReason, setReportReason] = useState('');

    const fetchPostAndComments = async () => {
        try {
            const postResponse = await getPost(worldviewId, postId);
            setPost(postResponse.data);
            const commentsResponse = await getComments(postId);
            setComments(commentsResponse.data);
        } catch (error) {
            console.error('Failed to fetch data', error);
            // Optionally navigate away or show error message
        }
    };

    useEffect(() => {
        fetchPostAndComments();
    }, [worldviewId, postId]); // Dependency array is correct

    const handleCommentSubmit = async (e) => {
        e.preventDefault();
        if (!newComment.trim()) return; // Prevent empty comments
        try {
            await createComment(postId, { content: newComment });
            setNewComment('');
            fetchPostAndComments(); // Refresh comments
        } catch (error) {
            console.error('Failed to create comment', error);
            alert('Failed to submit comment.');
        }
    };

    // ✅ Defined missing handleRecommend function
    const handleRecommend = async () => {
        try {
            await recommendPost(worldviewId, postId);
            fetchPostAndComments(); // Refresh post to show new recommendation count
        } catch (error) {
            console.error('Failed to recommend post', error);
            alert('Failed to recommend post. Already recommended?');
        }
    };

    const handleBookmark = async () => {
        try {
            await addBookmark({ type: 'POST', id: postId });
            alert('Bookmarked!');
        } catch (error) {
            alert('Failed to add bookmark. Already bookmarked?');
        }
    };

    const handleReportSubmit = async (e) => {
        e.preventDefault();
        if (!reportReason.trim()) return; // Prevent empty reports
        try {
            await createReport({
                reason: reportReason,
                worldviewId: worldviewId, // Ensure worldviewId is passed correctly
                postId: postId
            });
            alert('Report submitted successfully');
            setShowReport(false);
            setReportReason('');
        } catch (error) {
            console.error('Failed to submit report', error);
            alert('Failed to submit report');
        }
    };

    if (!post) return <div className="text-center mt-10">Loading post...</div>;

    return (
        <div className="max-w-3xl mx-auto bg-white p-8 rounded-lg shadow-md">
            {/* --- Post Content --- */}
            <h2 className="text-4xl font-bold text-gray-800 mb-2">{post.title}</h2>
            <p className="text-gray-500 mb-4">By {post.authorNickname} | Created at: {new Date(post.createdAt).toLocaleString()}</p>
            <div className="flex flex-wrap gap-2 mb-4">
                {(post.tags || []).map(tag => ( // Handle case where tags might be null/undefined initially
                    <span key={tag} className="bg-blue-100 text-blue-800 text-xs font-semibold mr-2 px-2.5 py-0.5 rounded-full">{tag}</span>
                ))}
            </div>
            {/* Using dangerouslySetInnerHTML can be risky if content isn't sanitized on the backend.
          If content is plain text, just use a <p> or <div>. If it contains HTML, ensure it's safe.
          For simplicity, assuming plain text for now. */}
            <div className="prose max-w-none mb-8 whitespace-pre-wrap">{post.content}</div>

            {/* --- Actions: Recommend, Bookmark, Report --- */}
            <div className="flex items-center space-x-4 mb-8">
                <button
                    onClick={handleRecommend}
                    className="px-6 py-2 font-semibold text-white bg-blue-500 rounded-md hover:bg-blue-600 transition duration-300 flex items-center space-x-2"
                >
                    {/* Simple thumb icon - consider using an icon library like react-icons */}
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path d="M2 10.5a1.5 1.5 0 113 0v6a1.5 1.5 0 01-3 0v-6zM6 10.333v5.43a2 2 0 001.106 1.79l.05.025A4 4 0 008.943 18h5.416a2 2 0 001.962-1.608l1.2-6A2 2 0 0015.562 8H12V4a2 2 0 00-2-2 1 1 0 00-1 1v.667a4 4 0 01-.8 2.4L6.8 7.933a2 2 0 00-.26 1.4z" />
                    </svg>
                    <span>Recommend ({post.recommendations})</span>
                </button>
                <button
                    onClick={handleBookmark}
                    className="px-6 py-2 font-semibold text-white bg-yellow-500 rounded-md hover:bg-yellow-600 transition duration-300 flex items-center space-x-2"
                >
                    {/* Simple bookmark icon */}
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path d="M5 4a2 2 0 012-2h6a2 2 0 012 2v14l-5-3.5L5 18V4z" />
                    </svg>
                    <span>Bookmark</span>
                </button>
                <button
                    onClick={() => setShowReport(!showReport)}
                    className="text-sm text-gray-500 hover:text-red-600 hover:underline"
                >
                    Report Post
                </button>
            </div>

            {/* --- Report Form (Conditional) --- */}
            {showReport && (
                <form onSubmit={handleReportSubmit} className="mb-8 p-4 border rounded-md bg-gray-50">
                    <label className="block text-sm font-medium text-gray-700 mb-1">Reason for reporting:</label>
                    <textarea
                        value={reportReason}
                        onChange={e => setReportReason(e.target.value)}
                        placeholder="Please provide details..."
                        className="w-full px-4 py-2 mt-1 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        rows="3"
                        required
                    />
                    <button type="submit" className="mt-2 px-4 py-2 font-semibold text-white bg-red-500 rounded-md hover:bg-red-600">Submit Report</button>
                </form>
            )}

            {/* --- Comments Section --- */}
            <h3 className="text-2xl font-bold text-gray-800 mb-4">Comments</h3>
            <div className="space-y-4 mb-6">
                {comments.length > 0 ? comments.map((comment) => (
                    <div key={comment.id} className="p-4 border rounded-md bg-gray-50">
                        <p className="font-semibold text-gray-800">{comment.authorNickname}</p>
                        <p className="text-gray-700 mt-1 whitespace-pre-wrap">{comment.content}</p>
                        <p className="text-xs text-gray-400 mt-2">
                            {new Date(comment.createdAt).toLocaleString()} | Recommendations: {comment.recommendations}
                            {/* Optional: Add Recommend Comment Button */}
                            {/* <button onClick={() => handleRecommendComment(comment.id)} className="ml-2 text-blue-500 text-xs hover:underline">Recommend</button> */}
                        </p>
                    </div>
                )) : (
                    <p className="text-gray-500">No comments yet. Be the first to comment!</p>
                )}
            </div>

            {/* --- New Comment Form --- */}
            <form onSubmit={handleCommentSubmit}>
                <h4 className="text-lg font-semibold mb-2">Write a comment</h4>
                <textarea
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    placeholder="Join the discussion..."
                    className="w-full px-4 py-2 mt-1 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    rows="4"
                    required
                />
                <button type="submit" className="mt-2 px-6 py-2 font-semibold text-white bg-green-500 rounded-md hover:bg-green-600 transition duration-300">
                    Submit
                </button>
            </form>
        </div>
    );
};

export default PostDetail;