import React, { useState, useEffect } from 'react';
import { getWorldviews, searchWorldviews, searchWorldviewsByTag, getAllTags } from '../../api';
import { Link } from 'react-router-dom';

// 세계관 하나하나를 보여줄 카드 컴포넌트
const WorldviewCard = ({ worldview }) => {
    // ✅ 이미지 URL 처리 로직 추가
    let imageUrl = 'https://via.placeholder.com/400x200?text=No+Image'; // 기본 이미지
    if (worldview.coverImageUrl) {
        // URL이 http로 시작하는지 확인 (AI 생성 등 절대 경로)
        if (worldview.coverImageUrl.startsWith('http')) {
            imageUrl = worldview.coverImageUrl;
        } else {
            // 그렇지 않으면 백엔드 주소를 앞에 붙임 (업로드된 이미지)
            imageUrl = `http://localhost:8080${worldview.coverImageUrl}`;
        }
    }

    return (
        <div className="bg-white rounded-lg shadow-lg overflow-hidden transform hover:-translate-y-1 transition-all duration-300">
            <Link to={`/worldviews/${worldview.id}/posts`}>
                <img
                    className="h-48 w-full object-cover"
                    src={imageUrl} // ✅ 수정된 imageUrl 사용
                    alt={worldview.name}
                    onError={(e) => { e.target.onerror = null; e.target.src='https://via.placeholder.com/400x200?text=Load+Error'; }}
                />
            </Link>
            <div className="p-6">
                <h3 className="text-xl font-bold text-gray-800 mb-2">{worldview.name}</h3>
                <p className="text-gray-600 text-sm mb-4">By {worldview.creatorNickname}</p>
                <div className="flex flex-wrap gap-2 mb-4 h-14 overflow-y-auto">
                    {worldview.tags?.map(tag => (
                        <span key={tag} className="bg-blue-100 text-blue-800 text-xs font-semibold mr-2 px-2.5 py-0.5 rounded-full">{tag}</span>
                    ))}
                </div>
                <Link
                    to={`/worldviews/${worldview.id}/posts`}
                    className="inline-block w-full text-center bg-blue-500 text-white font-bold py-2 px-4 rounded-full hover:bg-blue-600 transition duration-300"
                >
                    Enter Worldview
                </Link>
            </div>
        </div>
    );
};


const WorldviewList = () => {
    const [worldviews, setWorldviews] = useState([]);
    const [query, setQuery] = useState('');
    const [tags, setTags] = useState([]);
    const [selectedTag, setSelectedTag] = useState('');

    const fetchWorldviews = async () => {
        try {
            const response = await getWorldviews();
            setWorldviews(response.data);
        } catch (error) {
            console.error('Failed to fetch worldviews', error);
        }
    };

    useEffect(() => {
        fetchWorldviews();
        const fetchTags = async () => {
            try {
                const { data } = await getAllTags();
                setTags(data);
            } catch (error) {
                console.error('Failed to fetch tags', error);
            }
        };
        fetchTags();
    }, []);

    const handleSearch = async (e) => {
        e.preventDefault();
        setSelectedTag('');
        if (query.trim() === '') {
            fetchWorldviews();
            return;
        }
        try {
            const response = await searchWorldviews(query);
            setWorldviews(response.data);
        } catch (error) {
            console.error('Failed to search worldviews', error);
        }
    };

    const handleTagSearch = async (tag) => {
        setQuery('');
        setSelectedTag(tag);
        try {
            const response = await searchWorldviewsByTag(tag);
            setWorldviews(response.data);
        } catch (error) {
            console.error('Failed to search by tag', error);
        }
    };

    return (
        <div>
            <h2 className="text-3xl font-bold text-gray-800 mb-6">Explore Worldviews</h2>

            {/* 검색 폼 */}
            <form onSubmit={handleSearch} className="mb-6 flex">
                <input
                    type="text"
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    placeholder="Search by name or keyword..."
                    className="w-full px-4 py-2 border rounded-l-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <button type="submit" className="px-4 py-2 font-semibold text-white bg-blue-500 rounded-r-md hover:bg-blue-600 transition duration-300">
                    Search
                </button>
            </form>

            {/* 태그 검색 영역 */}
            <div className="mb-6 flex flex-wrap gap-2">
                <span className="font-semibold text-gray-700 py-1">Popular Tags:</span>
                {tags.map(tag => (
                    <button
                        key={tag}
                        onClick={() => handleTagSearch(tag)}
                        className={`text-xs font-semibold px-2.5 py-1 rounded-full ${selectedTag === tag ? 'bg-blue-500 text-white' : 'bg-blue-100 text-blue-800 hover:bg-blue-200'}`}
                    >
                        {tag}
                    </button>
                ))}
            </div>

            {/* 세계관 카드 그리드 */}
            {worldviews.length > 0 ? (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                    {worldviews.map((worldview) => (
                        <WorldviewCard key={worldview.id} worldview={worldview} />
                    ))}
                </div>
            ) : (
                <div className="text-center text-gray-500 mt-10">
                    <p>No worldviews found. Why not create one?</p>
                </div>
            )}
        </div>
    );
};

export default WorldviewList;