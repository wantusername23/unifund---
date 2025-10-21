import React, { useState, useEffect } from 'react';
import { getVotes, createVote, castVote } from '../../api';

const VoteList = ({ worldviewId, isCreator }) => {
    const [votes, setVotes] = useState([]);
    const [topic, setTopic] = useState('');
    const [options, setOptions] = useState(['', '']); // Default 2 options
    const [endsAt, setEndsAt] = useState('');

    const fetchVotes = async () => {
        try {
            const response = await getVotes(worldviewId);
            setVotes(response.data);
        } catch (error) {
            console.error("Failed to fetch votes", error);
        }
    };

    useEffect(() => {
        fetchVotes();
    }, [worldviewId]);

    const handleVote = async (voteId, optionId) => {
        try {
            await castVote(worldviewId, voteId, optionId);
            fetchVotes(); // Refresh list after voting
        } catch(err) {
            alert(err.response?.data?.message || 'Failed to vote. Only members can vote.');
        }
    };

    const handleAddOption = () => {
        setOptions([...options, '']);
    };

    const handleOptionChange = (index, value) => {
        const newOptions = [...options];
        newOptions[index] = value;
        setOptions(newOptions);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!endsAt) {
            alert("Please set an end date.");
            return;
        }
        // Filter out empty options before sending
        const validOptions = options.filter(option => option.trim() !== '');
        if (validOptions.length < 2) {
            alert("Please provide at least two valid options.");
            return;
        }

        try {
            await createVote(worldviewId, {
                topic,
                options: validOptions,
                // Ensure date is sent in ISO format expected by backend
                endsAt: new Date(endsAt).toISOString()
            });
            // Reset form
            setTopic('');
            setOptions(['', '']);
            setEndsAt('');
            fetchVotes(); // Refresh list
        } catch (error) {
            alert('Failed to create vote.');
        }
    };

    return (
        <div className="bg-white p-8 rounded-lg shadow-md mt-6">
            <h3 className="text-2xl font-bold text-gray-800 mb-4">Votes</h3>

            {isCreator && (
                <form onSubmit={handleSubmit} className="mb-6 p-4 border rounded-md bg-gray-50 space-y-4">
                    <h4 className="text-lg font-semibold">Create New Vote</h4>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Topic</label>
                        <input type="text" value={topic} onChange={e => setTopic(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md" required />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">End Date & Time</label>
                        <input type="datetime-local" value={endsAt} onChange={e => setEndsAt(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md" required />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Options</label>
                        {options.map((option, index) => (
                            <input
                                key={index}
                                type="text"
                                value={option}
                                onChange={e => handleOptionChange(index, e.target.value)}
                                className="w-full px-4 py-2 mt-1 border rounded-md"
                                placeholder={`Option ${index + 1}`}
                            />
                        ))}
                        <button type="button" onClick={handleAddOption} className="text-sm text-blue-500 hover:underline mt-2">Add Option</button>
                    </div>
                    <button type="submit" className="px-4 py-2 font-semibold text-white bg-blue-500 rounded-md hover:bg-blue-600">Create Vote</button>
                </form>
            )}

            <div className="space-y-6">
                {votes.map(vote => (
                    <div key={vote.id} className="p-4 border rounded-md">
                        <h4 className="text-xl font-semibold">{vote.topic}</h4>
                        <p className="text-sm text-gray-500">Created by: {vote.creatorNickname} | Ends at: {new Date(vote.endsAt).toLocaleString()}</p>
                        <ul className="mt-4 space-y-2">
                            {vote.options.map(option => (
                                <li key={option.id} className="flex justify-between items-center">
                                    <span>{option.text} ({option.voteCount} votes)</span>
                                    {/* ✅ Corrected closing tag here */}
                                    <button onClick={() => handleVote(vote.id, option.id)} className="px-3 py-1 bg-green-500 text-white rounded-md hover:bg-green-600 text-sm">Vote</button>
                                </li>
                            ))}
                        </ul>
                        <p className="text-sm font-semibold mt-2">Total Votes: {vote.totalVotes}</p>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default VoteList;