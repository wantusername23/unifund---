import React, { useState, useEffect } from 'react';
import { getBookmarks } from '../../api';
import { Link } from 'react-router-dom';

const BookmarkList = () => {
    const [bookmarks, setBookmarks] = useState([]);

    useEffect(() => {
        const fetchBookmarks = async () => {
            try {
                const { data } = await getBookmarks();
                setBookmarks(data);
            } catch (error) {
                console.error("Failed to fetch bookmarks", error);
            }
        };
        fetchBookmarks();
    }, []);

    return (
        <div className="max-w-2xl mx-auto bg-white p-8 rounded-lg shadow-md">
            <h2 className="text-3xl font-bold text-gray-800 mb-6">My Bookmarks</h2>
            <div className="space-y-4">
                {bookmarks.length > 0 ? (
                    bookmarks.map(bookmark => (
                        <div key={`${bookmark.type}-${bookmark.id}`} className="p-4 border rounded-md hover:bg-gray-50">
              <span className={`text-xs font-semibold px-2 py-1 rounded-full mr-2 ${bookmark.type === 'WORLDVIEW' ? 'bg-blue-100 text-blue-800' : 'bg-green-100 text-green-800'}`}>
                {bookmark.type}
              </span>
                            <Link to={bookmark.type === 'WORLDVIEW' ? `/worldviews/${bookmark.id}/posts` : `/worldviews/1/posts/${bookmark.id}`} className="text-blue-600 hover:underline">
                                {bookmark.title}
                            </Link>
                        </div>
                    ))
                ) : (
                    <p>No bookmarks yet.</p>
                )}
            </div>
        </div>
    );
};

export default BookmarkList;