import React, { useState, useEffect } from 'react';
import { useParams, Routes, Route, Link } from 'react-router-dom';
import { getWorldview, addBookmark } from '../../api';
import PostList from '../post/PostList';
import CharacterList from '../character/CharacterList';
import Timeline from '../timeline/Timeline';
import VoteList from '../vote/VoteList';
import Memberships from './Memberships'; // ✅ 추가
import Contributors from './Contributors'; // ✅ 추가

const WorldviewDetail = () => {
    const { id } = useParams();
    const [worldview, setWorldview] = useState(null);

    useEffect(() => {
        const fetchWorldview = async () => {
            try {
                const response = await getWorldview(id);
                setWorldview(response.data);
            } catch (error) {
                console.error('Failed to fetch worldview', error);
            }
        };
        fetchWorldview();
    }, [id]);

    const handleBookmark = async () => {
        try {
            await addBookmark({ type: 'WORLDVIEW', id: id });
            alert('Bookmarked!');
        } catch (error) {
            alert('Failed to add bookmark.');
        }
    };

    if (!worldview) return <div>Loading...</div>;

    return (
        <div>
            <div className="bg-white p-6 rounded-lg shadow-md mb-6">
                <h2 className="text-3xl font-bold">{worldview.name}</h2>
                <p className="text-gray-600">By {worldview.creatorNickname}</p>
                <p className="mt-4">{worldview.description}</p>
                <div className="mt-4 flex space-x-4">
                    <button onClick={handleBookmark} className="bg-yellow-500 hover:bg-yellow-600 text-white font-bold py-2 px-4 rounded-full transition duration-300">
                        Bookmark
                    </button>
                    {/* ✅ 크리에이터인 경우 관리자 대시보드 링크 표시 */}
                    {worldview.isCreator && (
                        <Link
                            to={`/worldviews/${id}/admin/revenue`}
                            className="bg-purple-500 hover:bg-purple-600 text-white font-bold py-2 px-4 rounded-full transition duration-300"
                        >
                            Creator Dashboard
                        </Link>
                    )}
                </div>
            </div>

            {/* ✅ 멤버십 컴포넌트 추가 */}
            <Memberships worldviewId={id} />

            <nav className="bg-white p-4 rounded-lg shadow-md mt-6 flex space-x-4">
                <Link to={`/worldviews/${id}/posts`} className="text-gray-600 hover:text-blue-500 font-semibold">Posts</Link>
                <Link to={`/worldviews/${id}/characters`} className="text-gray-600 hover:text-blue-500 font-semibold">Characters</Link>
                <Link to={`/worldviews/${id}/timeline`} className="text-gray-600 hover:text-blue-500 font-semibold">Timeline</Link>
                <Link to={`/worldviews/${id}/votes`} className="text-gray-600 hover:text-blue-500 font-semibold">Votes</Link>
                <Link to={`/worldviews/${id}/contributors`} className="text-gray-600 hover:text-blue-500 font-semibold">Contributors</Link>
            </nav>

            <Routes>
                <Route path="posts" element={<PostList worldviewId={id} isCreator={worldview.isCreator} />} />
                <Route path="characters" element={<CharacterList worldviewId={id} isCreator={worldview.isCreator} />} />
                <Route path="timeline" element={<Timeline worldviewId={id} isCreator={worldview.isCreator} />} />
                <Route path="votes" element={<VoteList worldviewId={id} isCreator={worldview.isCreator} />} />
                <Route path="contributors" element={<Contributors worldviewId={id} isCreator={worldview.isCreator} />} />
            </Routes>
        </div>
    );
};

export default WorldviewDetail;