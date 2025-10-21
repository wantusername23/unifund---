import React, { useState, useEffect } from 'react';
// ✅ getPopularPosts API 함수는 전역 인기글 용이므로 여기서는 사용하지 않습니다.
import { getPosts, getPendingPosts, approvePost } from '../../api';
import { Link } from 'react-router-dom';

const PostItem = ({ post, worldviewId }) => (
    <div className="p-4 border rounded-md hover:bg-gray-50">
        <Link to={`/worldviews/${worldviewId}/posts/${post.id}`} className="text-lg font-semibold text-blue-600 hover:underline">
            {post.title} {post.isNotice && <span className="text-red-500 text-sm ml-2">[Notice]</span>}
        </Link>
        <p className="text-sm text-gray-500">
            by {post.authorNickname} | Recommendations: {post.recommendations} {/* ✅ 추천 수 표시 */}
        </p>
    </div>
);

const PostList = ({ worldviewId, isCreator }) => {
    const [posts, setPosts] = useState([]);
    const [pendingPosts, setPendingPosts] = useState([]);
    // ✅ boardType 상태에 'POPULAR' 추가
    const [boardType, setBoardType] = useState('FREE'); // FREE, WORKS, POPULAR, PENDING
    const [popularPosts, setPopularPosts] = useState([]); // ✅ 인기글 상태 추가

    const fetchPosts = async (type) => {
        try {
            const { data } = await getPosts(worldviewId, type);
            return data;
        } catch (error) {
            console.error(`Failed to fetch ${type} posts`, error);
            return []; // 오류 발생 시 빈 배열 반환
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
        const loadData = async () => {
            // ✅ Popular 탭 로직 분리
            if (boardType === 'POPULAR') {
                // FREE 와 WORKS 게시판 글을 모두 가져옵니다.
                const freePosts = await fetchPosts('FREE');
                const worksPosts = await fetchPosts('WORKS');
                // 두 게시판 글을 합친 후 추천수 20개 이상인 글만 필터링합니다. (distributeRevenue 로직 참고)
                const allPosts = [...freePosts, ...worksPosts];
                setPopularPosts(allPosts.filter(post => post.recommendations >= 20));
            } else if (boardType === 'PENDING') {
                fetchPendingPosts();
            } else { // FREE or WORKS
                const fetchedPosts = await fetchPosts(boardType);
                setPosts(fetchedPosts);
            }
        };

        loadData();
    }, [worldviewId, boardType, isCreator]); // boardType 변경 시 데이터 다시 로드

    const handleApprove = async (postId) => {
        try {
            await approvePost(worldviewId, postId);
            fetchPendingPosts(); // 목록 새로고침
        } catch (error) {
            alert('Failed to approve post');
        }
    };

    // ✅ 현재 선택된 탭에 따라 보여줄 게시글 목록 결정
    const currentPosts = boardType === 'POPULAR' ? popularPosts : boardType === 'PENDING' ? pendingPosts : posts;

    return (
        <div className="bg-white p-8 rounded-lg shadow-md mt-6">
            <div className="flex justify-between items-center mb-4">
                <div className="flex space-x-4 border-b">
                    <button onClick={() => setBoardType('FREE')} className={`py-2 px-4 ${boardType === 'FREE' ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-500'} font-semibold`}>Free Board</button>
                    <button onClick={() => setBoardType('WORKS')} className={`py-2 px-4 ${boardType === 'WORKS' ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-500'} font-semibold`}>Works</button>
                    {/* ✅ Popular 탭 버튼 추가 */}
                    <button onClick={() => setBoardType('POPULAR')} className={`py-2 px-4 ${boardType === 'POPULAR' ? 'border-b-2 border-yellow-500 text-yellow-600' : 'text-gray-500'} font-semibold`}>Popular</button>
                    {isCreator && (
                        <button onClick={() => setBoardType('PENDING')} className={`py-2 px-4 ${boardType === 'PENDING' ? 'border-b-2 border-red-500 text-red-500' : 'text-gray-500'} font-semibold`}>Pending ({pendingPosts.length})</button>
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
                {/* ✅ currentPosts 사용 */}
                {currentPosts.length > 0 ? (
                    boardType === 'PENDING' ? (
                        // Pending 탭 렌더링
                        currentPosts.map(post => (
                            <div key={post.id} className="p-4 border rounded-md flex justify-between items-center">
                                <div>
                                    <Link to={`/worldviews/${worldviewId}/posts/${post.id}`} className="text-lg font-semibold text-blue-600 hover:underline">{post.title}</Link>
                                    <p className="text-sm text-gray-500">by {post.authorNickname}</p>
                                </div>
                                <button onClick={() => handleApprove(post.id)} className="px-3 py-1 bg-green-500 text-white rounded-md hover:bg-green-600">Approve</button>
                            </div>
                        ))
                    ) : (
                        // FREE, WORKS, POPULAR 탭 렌더링
                        currentPosts.map(post => <PostItem key={post.id} post={post} worldviewId={worldviewId} />)
                    )
                ) : (
                    <p className="text-gray-500 text-center py-4">
                        {boardType === 'PENDING' ? 'No pending posts.' :
                            boardType === 'POPULAR' ? 'No popular posts (20+ recommendations) in this worldview yet.' :
                                `No posts in this board yet.`}
                    </p>
                )}
            </div>
        </div>
    );
};

export default PostList;