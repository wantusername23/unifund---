import React, { useState } from 'react';
// ✅ createWorldviewWithUrl 추가
import { createWorldviewWithUpload, createWorldviewWithUrl } from '../../api';
import { useNavigate } from 'react-router-dom';

const CreateWorldview = () => {
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [keywords, setKeywords] = useState('');
    const [imageSource, setImageSource] = useState('upload'); // 'upload' or 'ai'
    const [file, setFile] = useState(null);
    const [lowTierName, setLowTierName] = useState('Basic Supporter');
    const [lowTierPrice, setLowTierPrice] = useState(5);
    const [highTierName, setHighTierName] = useState('Premium Contributor');
    const [highTierPrice, setHighTierPrice] = useState(15);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        // 공통 요청 데이터 구성
        const worldviewRequestBase = {
            name,
            description,
            keywords,
            // 멤버십 정보 포함
            lowTier: { name: lowTierName, price: lowTierPrice, description: 'Standard benefits' },
            highTier: { name: highTierName, price: highTierPrice, description: 'Enhanced benefits' },
            tags: keywords.split(',').map(tag => tag.trim()).filter(Boolean)
        };

        try {
            if (imageSource === 'upload') {
                if (!file) {
                    setError('Please select an image file to upload.');
                    setIsLoading(false);
                    return;
                }
                const formData = new FormData();
                // 백엔드가 JSON 문자열을 받을 수 있도록 수정
                formData.append("request", new Blob([JSON.stringify({ ...worldviewRequestBase, coverImageUrl: '' })], { type: "application/json" }));
                formData.append("file", file);
                await createWorldviewWithUpload(formData);
            } else { // AI 생성 옵션
                // 실제 AI 호출 대신 placeholder URL 사용
                const aiImageUrl = `https://via.placeholder.com/600x300.png?text=AI+Image+for+${encodeURIComponent(keywords.split(',')[0] || name)}`;
                const requestData = { ...worldviewRequestBase, coverImageUrl: aiImageUrl };
                await createWorldviewWithUrl(requestData);
            }
            navigate('/'); // 성공 시 홈으로 이동
        } catch (error) {
            console.error('Failed to create worldview', error);
            setError('Failed to create worldview. Please check your input and try again.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="max-w-2xl mx-auto bg-white p-8 rounded-lg shadow-md">
            <h2 className="text-3xl font-bold text-gray-800 mb-6">Create New Worldview</h2>
            {error && <p className="text-red-500 text-sm mb-4">{error}</p>}
            <form onSubmit={handleSubmit} className="space-y-6">
                {/* 기본 정보 */}
                <div>
                    <label className="block text-sm font-medium text-gray-700">Worldview Name</label>
                    <input type="text" value={name} onChange={e => setName(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" required />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Description</label>
                    <textarea value={description} onChange={e => setDescription(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" rows="4" required />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Keywords / Tags (comma-separated)</label>
                    <input type="text" value={keywords} onChange={e => setKeywords(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500" placeholder="e.g., fantasy, magic, medieval" required />
                </div>

                {/* 이미지 소스 선택 */}
                <div className="border-t pt-6">
                    <label className="block text-sm font-medium text-gray-700 mb-2">Cover Image</label>
                    <div className="flex items-center space-x-4">
                        <label className="flex items-center">
                            <input type="radio" name="imageSource" value="upload" checked={imageSource === 'upload'} onChange={() => setImageSource('upload')} className="form-radio h-4 w-4 text-blue-600" />
                            <span className="ml-2 text-sm text-gray-700">Upload Image</span>
                        </label>
                        <label className="flex items-center">
                            <input type="radio" name="imageSource" value="ai" checked={imageSource === 'ai'} onChange={() => setImageSource('ai')} className="form-radio h-4 w-4 text-blue-600" />
                            <span className="ml-2 text-sm text-gray-700">AI Generate (based on keywords)</span>
                        </label>
                    </div>
                    {imageSource === 'upload' && (
                        <div className="mt-4">
                            <input type="file" onChange={e => setFile(e.target.files[0])} className="w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100" />
                        </div>
                    )}
                    {imageSource === 'ai' && (
                        <p className="mt-4 text-sm text-gray-500">An image will be generated based on your keywords when you submit.</p>
                    )}
                </div>

                {/* 멤버십 설정 */}
                <div className="border-t pt-6 grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                        <h4 className="text-lg font-semibold mb-2 text-gray-800">Low Tier Membership</h4>
                        <label className="block text-sm font-medium text-gray-700">Tier Name</label>
                        <input type="text" value={lowTierName} onChange={e => setLowTierName(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md" required />
                        <label className="block text-sm font-medium text-gray-700 mt-2">Price ($)</label>
                        <input type="number" value={lowTierPrice} onChange={e => setLowTierPrice(Number(e.target.value))} className="w-full px-4 py-2 mt-1 border rounded-md" required min="0" />
                    </div>
                    <div>
                        <h4 className="text-lg font-semibold mb-2 text-gray-800">High Tier Membership</h4>
                        <label className="block text-sm font-medium text-gray-700">Tier Name</label>
                        <input type="text" value={highTierName} onChange={e => setHighTierName(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md" required />
                        <label className="block text-sm font-medium text-gray-700 mt-2">Price ($)</label>
                        <input type="number" value={highTierPrice} onChange={e => setHighTierPrice(Number(e.target.value))} className="w-full px-4 py-2 mt-1 border rounded-md" required min="0" />
                    </div>
                </div>

                {/* 제출 버튼 */}
                <button
                    type="submit"
                    disabled={isLoading}
                    className="w-full py-3 px-4 font-semibold text-white bg-blue-600 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition duration-300 disabled:opacity-50"
                >
                    {isLoading ? 'Creating...' : 'Create Worldview'}
                </button>
            </form>
        </div>
    );
};

export default CreateWorldview;