import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import {
    getAdminWorldview,
    getDistributionHistory,
    distributeRevenue,
    addContributor,
    getContributors,
    addMembershipTier,
    getPendingPosts,
    approvePost
} from '../../api';

// 1. 수익 관리 탭
const RevenueTab = ({ worldviewId }) => {
    const [adminData, setAdminData] = useState(null);
    const [history, setHistory] = useState([]);

    const fetchData = async () => {
        try {
            const adminRes = await getAdminWorldview(worldviewId);
            setAdminData(adminRes.data);
            const historyRes = await getDistributionHistory(worldviewId);
            setHistory(historyRes.data);
        } catch (error) { // ✅ 여기를 수정했습니다: (error)_ -> (error) {
            console.error("Failed to fetch admin data", error);
            alert("You do not have permission to view this page.");
        }
    };

    useEffect(() => {
        fetchData();
    }, [worldviewId]);

    const handleDistribute = async () => {
        try {
            await distributeRevenue(worldviewId);
            alert('Revenue distribution started!');
            fetchData();
        } catch (error) {
            alert('Failed to distribute revenue. Is the revenue pool positive?');
        }
    };

    if (!adminData) return <div className="text-center mt-10">Loading revenue data...</div>;

    return (
        <div className="space-y-6">
            <div className="p-6 border rounded-lg bg-gray-50">
                <h3 className="text-xl font-semibold text-gray-700">Current Revenue Pool</h3>
                <p className="text-4xl font-bold mt-2 text-blue-600">${adminData.revenuePool.toFixed(2)}</p>
                <button
                    onClick={handleDistribute}
                    disabled={adminData.revenuePool <= 0}
                    className="mt-4 px-6 py-2 font-semibold text-white bg-green-500 rounded-md hover:bg-green-600 transition duration-300 disabled:bg-gray-400"
                >
                    Distribute Revenue
                </button>
            </div>
            <div>
                <h3 className="text-xl font-semibold">Distribution History</h3>
                <ul className="mt-4 space-y-2">
                    {history.length > 0 ? history.map(item => (
                        <li key={item.id} className="p-4 border rounded-md flex justify-between">
                            <span>Distributed ${item.totalAmount.toFixed(2)}</span>
                            <span className="text-gray-500">{new Date(item.distributionDate).toLocaleString()}</span>
                        </li>
                    )) : <p className="text-gray-500">No distribution history.</p>}
                </ul>
            </div>
        </div>
    );
};

// 2. 공동 창작자 관리 탭
const ContributorTab = ({ worldviewId }) => {
    const [contributors, setContributors] = useState([]);
    const [email, setEmail] = useState('');
    const [permission, setPermission] = useState('VIEWER');

    const fetchContributors = async () => {
        try {
            const { data } = await getContributors(worldviewId);
            setContributors(data);
        } catch (error) {
            console.error("Failed to fetch contributors", error);
        }
    };

    useEffect(() => {
        fetchContributors();
    }, [worldviewId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await addContributor(worldviewId, { userEmail: email, permission });
            alert('Contributor added!');
            setEmail('');
            setPermission('VIEWER');
            fetchContributors();
        } catch (error) {
            alert('Failed to add contributor. Check email and ensure they are not already a contributor.');
        }
    };

    return (
        <div className="space-y-6">
            <form onSubmit={handleSubmit} className="p-6 border rounded-lg bg-gray-50 space-y-4">
                <h3 className="text-xl font-semibold">Add Contributor</h3>
                <div>
                    <label className="block text-sm font-medium text-gray-700">User Email</label>
                    <input type="email" value={email} onChange={e => setEmail(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md" required />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Permission</label>
                    <select value={permission} onChange={e => setPermission(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md bg-white">
                        <option value="VIEWER">VIEWER (Read-only)</option>
                        <option value="EDITOR">EDITOR (Can edit worldview & create posts)</option>
                    </select>
                </div>
                <button type="submit" className="px-6 py-2 font-semibold text-white bg-blue-500 rounded-md hover:bg-blue-600">Add</button>
            </form>
            <div>
                <h3 className="text-xl font-semibold">Current Contributors</h3>
                <ul className="mt-4 space-y-2">
                    {contributors.map(con => (
                        <li key={con.userId} className="p-4 border rounded-md flex justify-between items-center">
                            <span className="font-semibold">{con.nickname}</span>
                            <span className="text-sm bg-gray-200 text-gray-700 px-3 py-1 rounded-full">{con.permission}</span>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

// 3. 멤버십 티어 관리 탭
const MembershipTab = ({ worldviewId }) => {
    const [name, setName] = useState('');
    const [price, setPrice] = useState(0);
    const [description, setDescription] = useState('');
    const [level, setLevel] = useState(1);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await addMembershipTier(worldviewId, { name, price, description, level });
            alert('Membership tier created!');
            setName('');
            setPrice(0);
            setDescription('');
            setLevel(1);
        } catch (error) {
            alert('Failed to create membership tier.');
        }
    };

    return (
        <div className="p-6 border rounded-lg bg-gray-50 space-y-4">
            <h3 className="text-xl font-semibold">Create New Membership Tier</h3>
            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700">Tier Name (e.g., Bronze, Gold)</label>
                    <input type="text" value={name} onChange={e => setName(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md" required />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Price ($)</label>
                    <input type="number" value={price} onChange={e => setPrice(Number(e.target.value))} className="w-full px-4 py-2 mt-1 border rounded-md" required />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Description</label>
                    <textarea value={description} onChange={e => setDescription(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md" rows="3" required />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Level (e.g., 1, 2, 3)</label>
                    <input type="number" value={level} onChange={e => setLevel(Number(e.target.value))} className="w-full px-4 py-2 mt-1 border rounded-md" required />
                </div>
                <button type="submit" className="px-6 py-2 font-semibold text-white bg-blue-500 rounded-md hover:bg-blue-600">Create Tier</button>
            </form>
        </div>
    );
};

// 4. 게시글 승인 탭
const PendingPostsTab = ({ worldviewId }) => {
    const [pendingPosts, setPendingPosts] = useState([]);

    const fetchPendingPosts = async () => {
        try {
            const { data } = await getPendingPosts(worldviewId);
            setPendingPosts(data);
        } catch (error) {
            console.error("Failed to fetch pending posts", error);
        }
    };

    useEffect(() => {
        fetchPendingPosts();
    }, [worldviewId]);

    const handleApprove = async (postId) => {
        try {
            await approvePost(worldviewId, postId);
            alert('Post approved!');
            fetchPendingPosts(); // 목록 새로고침
        } catch (error) {
            alert('Failed to approve post');
        }
    };

    return (
        <div className="space-y-4">
            <h3 className="text-xl font-semibold">Pending Posts for Approval</h3>
            {pendingPosts.length > 0 ? (
                pendingPosts.map(post => (
                    <div key={post.id} className="p-4 border rounded-md flex justify-between items-center">
                        <div>
                            <p className="text-lg font-semibold">{post.title}</p>
                            <p className="text-sm text-gray-500">by {post.authorNickname}</p>
                        </div>
                        <button onClick={() => handleApprove(post.id)} className="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600">Approve</button>
                    </div>
                ))
            ) : <p className="text-gray-500">No pending posts.</p>}
        </div>
    );
};

// --- 메인 대시보드 컴포넌트 ---
const CreatorDashboard = () => {
    const { id } = useParams();
    const location = useLocation();
    const navigate = useNavigate();

    // URL 경로에 따라 현재 탭을 결정
    const getCurrentTab = () => {
        const path = location.pathname.split('/')[4];
        return path || 'revenue';
    };
    const [tab, setTab] = useState(getCurrentTab());

    const handleTabChange = (tabName) => {
        setTab(tabName);
        navigate(`/worldviews/${id}/admin/${tabName}`);
    };

    return (
        <div className="max-w-4xl mx-auto bg-white p-8 rounded-lg shadow-md">
            <h2 className="text-3xl font-bold text-gray-800 mb-6">Creator Dashboard</h2>
            <div className="flex space-x-1 border-b mb-6">
                <button onClick={() => handleTabChange('revenue')} className={`py-2 px-4 ${tab === 'revenue' ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-500'} font-semibold`}>Revenue</button>
                <button onClick={() => handleTabChange('pending')} className={`py-2 px-4 ${tab === 'pending' ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-500'} font-semibold`}>Pending Posts</button>
                <button onClick={() => handleTabChange('contributors')} className={`py-2 px-4 ${tab === 'contributors' ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-500'} font-semibold`}>Contributors</button>
                <button onClick={() => handleTabChange('membership')} className={`py-2 px-4 ${tab === 'membership' ? 'border-b-2 border-blue-500 text-blue-500' : 'text-gray-500'} font-semibold`}>Membership</button>
            </div>

            <div>
                {tab === 'revenue' && <RevenueTab worldviewId={id} />}
                {tab === 'pending' && <PendingPostsTab worldviewId={id} />}
                {tab === 'contributors' && <ContributorTab worldviewId={id} />}
                {tab === 'membership' && <MembershipTab worldviewId={id} />}
            </div>
        </div>
    );
};

export default CreatorDashboard;