import React, { useState, useEffect } from 'react';
import { useParams, Routes, Route, Link, useLocation } from 'react-router-dom';
// ✅ searchInWorldview 추가
import { getWorldview, addBookmark, searchInWorldview } from '../../api';
import PostList from '../post/PostList';
import CharacterList from '../character/CharacterList';
import Timeline from '../timeline/Timeline';
import VoteList from '../vote/VoteList';
import Memberships from './Memberships';
import Contributors from './Contributors';
import PostItem from '../post/PostItem'; // ✅ 검색 결과 표시용 PostItem 컴포넌트 임포트

const WorldviewDetail = () => {
    const { id } = useParams();
    const location = useLocation(); // 현재 경로 확인용
    const [worldview, setWorldview] = useState(null);
    const [searchQuery, setSearchQuery] = useState(''); // ✅ 검색어 상태
    const [searchResults, setSearchResults] = useState(null); // ✅ 검색 결과 상태 (null: 검색 안함, []: 결과 없음, [...]: 결과 있음)
    const [isSearching, setIsSearching] = useState(false); // ✅ 검색 로딩 상태

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
        // 페이지 이동 시 검색 결과 초기화
        setSearchResults(null);
        setSearchQuery('');
    }, [id, location.pathname]); // id나 내부 경로가 바뀔 때 실행

    const handleBookmark = async () => { /* ... (기존 코드) ... */ };

    // ✅ 검색 실행 함수
    const handleSearchSubmit = async (e) => {
        e.preventDefault();
        if (!searchQuery.trim()) {
            setSearchResults(null); // 검색어 없으면 검색 결과 지움
            return;
        }
        setIsSearching(true);
        try {
            const response = await searchInWorldview(id, searchQuery);
            setSearchResults(response.data);
        } catch (error) {
            console.error('Search failed:', error);
            setSearchResults([]); // 오류 시 빈 결과 표시
        } finally {
            setIsSearching(false);
        }
    };

    // ✅ 검색 결과 지우는 함수
    const clearSearch = () => {
        setSearchQuery('');
        setSearchResults(null);
    };

    if (!worldview) return <div className="text-center mt-10">Loading worldview...</div>;

    // 현재 활성화된 탭 이름 찾기 (스타일링용)
    const currentTab = location.pathname.split('/')[3] || 'posts';

    return (
        <div>
            {/* --- 세계관 정보 및 액션 버튼 --- */}
            <div className="bg-white p-6 rounded-lg shadow-md mb-6">
                <h2 className="text-3xl font-bold">{worldview.name}</h2>
                <p className="text-gray-600">By {worldview.creatorNickname}</p>
                <p className="mt-4">{worldview.description}</p>
                <div className="mt-4 flex space-x-4 items-center">
                    <button onClick={handleBookmark} className="bg-yellow-500 hover:bg-yellow-600 text-white font-bold py-2 px-4 rounded-full transition duration-300">
                        Bookmark
                    </button>
                    {worldview.isCreator && (
                        <Link
                            to={`/worldviews/${id}/admin/revenue`}
                            className="bg-purple-500 hover:bg-purple-600 text-white font-bold py-2 px-4 rounded-full transition duration-300"
                        >
                            Creator Dashboard
                        </Link>
                    )}
                    {/* ✅ 세계관 내 검색 폼 */}
                    <form onSubmit={handleSearchSubmit} className="flex-grow ml-auto">
                        <div className="relative">
                            <input
                                type="search"
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                placeholder="Search posts & comments in this worldview..."
                                className="w-full px-4 py-2 border rounded-full focus:outline-none focus:ring-2 focus:ring-blue-300"
                            />
                            <button type="submit" className="absolute right-0 top-0 mt-2 mr-3 text-blue-500 hover:text-blue-700">
                                <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fillRule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clipRule="evenodd"></path></svg>
                            </button>
                        </div>
                    </form>
                    {/* ✅ 검색 결과 있을 때 지우기 버튼 */}
                    {searchResults !== null && (
                        <button onClick={clearSearch} className="text-sm text-gray-500 hover:text-red-600 hover:underline ml-2">Clear Search</button>
                    )}
                </div>
            </div>

            {/* --- 멤버십 --- */}
            <Memberships worldviewId={id} />

            {/* --- 검색 결과 또는 탭 컨텐츠 표시 --- */}
            {searchResults !== null ? (
                // ✅ 검색 결과 표시 영역
                <div className="bg-white p-8 rounded-lg shadow-md mt-6">
                    <h3 className="text-2xl font-bold text-gray-800 mb-4">Search Results for "{searchQuery}" ({searchResults.length})</h3>
                    {isSearching ? <p>Searching...</p> : (
                        searchResults.length > 0 ? (
                            <div className="space-y-4">
                                {/* 검색 결과를 PostItem으로 렌더링 */}
                                {searchResults.map(post => <PostItem key={post.id} post={post} worldviewId={id} />)}
                            </div>
                        ) : <p className="text-gray-500">No results found.</p>
                    )}
                </div>
            ) : (
                // ✅ 탭 네비게이션 및 컨텐츠 (기존 로직)
                <>
                    <nav className="bg-white p-4 rounded-lg shadow-md mt-6 flex space-x-4 sticky top-[80px] z-40"> {/* sticky top 조정 */}
                        <Link to={`/worldviews/${id}/posts`} className={`pb-2 ${currentTab === 'posts' ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-600'} hover:text-blue-500 font-semibold`}>Posts</Link>
                        <Link to={`/worldviews/${id}/characters`} className={`pb-2 ${currentTab === 'characters' ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-600'} hover:text-blue-500 font-semibold`}>Characters</Link>
                        <Link to={`/worldviews/${id}/timeline`} className={`pb-2 ${currentTab === 'timeline' ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-600'} hover:text-blue-500 font-semibold`}>Timeline</Link>
                        <Link to={`/worldviews/${id}/votes`} className={`pb-2 ${currentTab === 'votes' ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-600'} hover:text-blue-500 font-semibold`}>Votes</Link>
                        <Link to={`/worldviews/${id}/contributors`} className={`pb-2 ${currentTab === 'contributors' ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-600'} hover:text-blue-500 font-semibold`}>Contributors</Link>
                    </nav>

                    {/* 탭 컨텐츠 렌더링 */}
                    <Routes>
                        <Route path="posts" element={<PostList worldviewId={id} isCreator={worldview.isCreator} />} />
                        <Route path="characters" element={<CharacterList worldviewId={id} isCreator={worldview.isCreator} />} />
                        <Route path="timeline" element={<Timeline worldviewId={id} isCreator={worldview.isCreator} />} />
                        <Route path="votes" element={<VoteList worldviewId={id} isCreator={worldview.isCreator} />} />
                        <Route path="contributors" element={<Contributors worldviewId={id} />} /> {/* isCreator는 Contributors 내부에서 필요 없어짐 */}
                    </Routes>
                </>
            )}
        </div>
    );
};

export default WorldviewDetail;