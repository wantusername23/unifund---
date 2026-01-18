import React from 'react';
import { Link } from 'react-router-dom';

const PostItem = ({ post, worldviewId }) => (
    // Container with padding, border, rounded corners, and hover effect
    <div className="p-4 border border-gray-200 rounded-md hover:bg-gray-50 transition-colors duration-200">
        {/* Link to the full post detail page */}
        <Link to={`/worldviews/${worldviewId}/posts/${post.id}`} className="block">
            {/* Conditionally render the image if imageUrl exists */}
            {post.imageUrl && (
                <img
                    src={post.imageUrl}
                    alt={post.title}
                    className="w-full h-32 object-cover rounded-md mb-4" // Style for the image
                />
            )}
            {/* Post Title: Large, bold, blue, truncates if too long */}
            <h4 className="text-lg font-semibold text-blue-600 hover:underline truncate">
                {post.title}
                {/* Display [Notice] tag if applicable */}
                {post.isNotice && <span className="text-red-500 text-sm ml-2">[Notice]</span>}
            </h4>
        </Link>
        {/* Author and Recommendation count */}
        <p className="text-sm text-gray-500 mt-1">
            by {post.authorNickname} | Recommendations: {post.recommendations}
        </p>
        {/* Tags display area */}
        <div className="flex flex-wrap gap-1 mt-2">
            {/* Map through tags (handle if tags array is missing) */}
            {(post.tags || []).map(tag => (
                <span
                    key={tag}
                    className="bg-gray-200 text-gray-700 text-xs font-medium px-2 py-0.5 rounded-full"
                >
          {tag}
        </span>
            ))}
        </div>
    </div>
);

export default PostItem;
