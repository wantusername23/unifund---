import React, { useState, useEffect } from 'react';
import { createWorldviewWithUpload, createWorldviewWithUrl } from '../../api';
import { useNavigate } from 'react-router-dom';

const CreateWorldview = () => {
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [keywords, setKeywords] = useState('');
    const [imageSource, setImageSource] = useState('upload');
    const [file, setFile] = useState(null);
    const [preview, setPreview] = useState(''); // Upload preview URL
    const [aiPreviewUrl, setAiPreviewUrl] = useState(''); // AI preview URL
    const [lowTierName, setLowTierName] = useState('Basic Supporter');
    const [lowTierPrice, setLowTierPrice] = useState(5);
    const [highTierName, setHighTierName] = useState('Premium Contributor');
    const [highTierPrice, setHighTierPrice] = useState(15);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    // Effect for upload preview
    useEffect(() => {
        if (!file) {
            setPreview('');
            return;
        }
        const objectUrl = URL.createObjectURL(file);
        setPreview(objectUrl);
        // Cleanup function to revoke the object URL
        return () => URL.revokeObjectURL(objectUrl);
    }, [file]);

    // Generate AI Preview URL (Placeholder)
    const generateAiPreview = () => {
        if (!keywords.trim() && !name.trim()) {
            alert('Please enter keywords or a name first to generate an AI preview.');
            return;
        }
        const query = keywords.split(',')[0]?.trim() || name;
        const placeholderUrl = `https://via.placeholder.com/600x300.png?text=AI+Preview+for+${encodeURIComponent(query)}`;
        console.log("Setting AI Preview URL:", placeholderUrl); // For debugging
        setAiPreviewUrl(placeholderUrl);
    };

    // File input change handler
    const handleFileChange = (e) => {
        const selectedFile = e.target.files?.[0];
        setFile(selectedFile || null);
    };

    // Form submission handler
    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        const worldviewRequestBase = {
            name, description, keywords,
            lowTier: { name: lowTierName, price: lowTierPrice, description: 'Standard benefits' },
            highTier: { name: highTierName, price: highTierPrice, description: 'Enhanced benefits' },
            tags: keywords.split(',').map(tag => tag.trim()).filter(Boolean)
        };

        try {
            if (imageSource === 'upload') {
                if (!file) {
                    setError('Please select an image file to upload.'); setIsLoading(false); return;
                }
                const formData = new FormData();
                formData.append("request", new Blob([JSON.stringify({ ...worldviewRequestBase, coverImageUrl: '' })], { type: "application/json" }));
                formData.append("file", file);
                await createWorldviewWithUpload(formData);
            } else { // AI generation option
                const finalAiImageUrl = aiPreviewUrl || `https://via.placeholder.com/600x300.png?text=AI+Image+for+${encodeURIComponent(keywords.split(',')[0] || name)}`;
                console.log("Submitting with AI Image URL:", finalAiImageUrl);
                const requestData = { ...worldviewRequestBase, coverImageUrl: finalAiImageUrl };
                await createWorldviewWithUrl(requestData);
            }
            navigate('/');
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
                {/* --- Basic Info --- */}
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

                {/* --- Image Source & Preview --- */}
                <div className="border-t pt-6">
                    <label className="block text-sm font-medium text-gray-700 mb-2">Cover Image</label>
                    <div className="flex items-center space-x-4">
                        {/* Upload Radio */}
                        <label className="flex items-center cursor-pointer">
                            <input type="radio" name="imageSource" value="upload" checked={imageSource === 'upload'} onChange={() => { setImageSource('upload'); setAiPreviewUrl(''); /* Reset AI preview */ }} className="form-radio h-4 w-4 text-blue-600"/>
                            <span className="ml-2 text-sm text-gray-700">Upload Image</span>
                        </label>
                        {/* AI Radio */}
                        <label className="flex items-center cursor-pointer">
                            <input type="radio" name="imageSource" value="ai" checked={imageSource === 'ai'} onChange={() => { setImageSource('ai'); setFile(null); setPreview(''); /* Reset upload preview */ }} className="form-radio h-4 w-4 text-blue-600"/>
                            <span className="ml-2 text-sm text-gray-700">AI Generate (based on keywords)</span>
                        </label>
                    </div>

                    {/* Upload Input & Preview */}
                    {imageSource === 'upload' && (
                        <div className="mt-4 space-y-4">
                            <input type="file" accept="image/*" onChange={handleFileChange} className="w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100" />
                            {preview && (
                                // ✅ Corrected this div - removed extra '>'
                                <div>
                                    <p className="text-sm font-medium text-gray-600 mb-2">Upload Preview:</p>
                                    <img src={preview} alt="Upload Preview" className="rounded-md shadow-sm max-h-48 w-auto border"/>
                                </div>
                            )}
                        </div>
                    )}

                    {/* AI Generate & Preview */}
                    {imageSource === 'ai' && (
                        <div className="mt-4 space-y-4">
                            <button type="button" onClick={generateAiPreview} className="px-4 py-2 text-sm font-semibold text-white bg-purple-500 rounded-md hover:bg-purple-600 transition duration-300">
                                Generate AI Preview
                            </button>
                            {aiPreviewUrl && (
                                <div>
                                    <p className="text-sm font-medium text-gray-600 mb-2">AI Preview:</p>
                                    <img
                                        src={aiPreviewUrl}
                                        alt="AI Preview"
                                        className="rounded-md shadow-sm max-h-48 w-auto border"
                                        onError={(e) => console.error("AI Preview Image failed to load:", e.target.src)}
                                    />
                                </div>
                            )}
                        </div>
                    )}
                </div>

                {/* --- Membership Settings --- */}
                <div className="border-t pt-6 grid grid-cols-1 md:grid-cols-2 gap-6">
                    {/* Low Tier */}
                    <div>
                        <h4 className="text-lg font-semibold mb-2 text-gray-800">Low Tier Membership</h4>
                        <label className="block text-sm font-medium text-gray-700">Tier Name</label>
                        <input type="text" value={lowTierName} onChange={e => setLowTierName(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md" required />
                        <label className="block text-sm font-medium text-gray-700 mt-2">Price ($)</label>
                        <input type="number" value={lowTierPrice} onChange={e => setLowTierPrice(Number(e.target.value))} className="w-full px-4 py-2 mt-1 border rounded-md" required min="0" step="0.01"/>
                    </div>
                    {/* High Tier */}
                    <div>
                        <h4 className="text-lg font-semibold mb-2 text-gray-800">High Tier Membership</h4>
                        <label className="block text-sm font-medium text-gray-700">Tier Name</label>
                        <input type="text" value={highTierName} onChange={e => setHighTierName(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md" required />
                        <label className="block text-sm font-medium text-gray-700 mt-2">Price ($)</label>
                        <input type="number" value={highTierPrice} onChange={e => setHighTierPrice(Number(e.target.value))} className="w-full px-4 py-2 mt-1 border rounded-md" required min="0" step="0.01"/>
                    </div>
                </div>

                {/* --- Submit Button --- */}
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