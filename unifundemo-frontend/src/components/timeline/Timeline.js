import React, { useState, useEffect } from 'react';
import { getTimelineEvents, createTimelineEvent } from '../../api';

// ✅ isCreator prop 추가
const Timeline = ({ worldviewId, isCreator }) => {
    const [events, setEvents] = useState([]);
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [eventDate, setEventDate] = useState('');

    const fetchEvents = async () => {
        try {
            const response = await getTimelineEvents(worldviewId);
            setEvents(response.data);
        } catch (error) {
            console.error("Failed to fetch timeline", error);
        }
    };

    useEffect(() => {
        fetchEvents();
    }, [worldviewId]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await createTimelineEvent(worldviewId, { title, description, eventDate });
            setTitle('');
            setDescription('');
            setEventDate('');
            fetchEvents(); // 목록 새로고침
        } catch (error) {
            alert('Failed to create event. Only creators or editors can add events.');
        }
    };

    return (
        <div className="bg-white p-8 rounded-lg shadow-md mt-6">
            <h3 className="text-2xl font-bold text-gray-800 mb-4">Timeline</h3>

            {/* ✅ isCreator가 true일 때만 폼을 보여줌 */}
            {isCreator && (
                <form onSubmit={handleSubmit} className="mb-6 p-4 border rounded-md bg-gray-50 space-y-4">
                    <h4 className="text-lg font-semibold">Add New Event</h4>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Event Title</label>
                        <input type="text" value={title} onChange={e => setTitle(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md" required />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Event Date (e.g., "B.C. 100")</label>
                        <input type="text" value={eventDate} onChange={e => setEventDate(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md" required />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Description</label>
                        <textarea value={description} onChange={e => setDescription(e.target.value)} className="w-full px-4 py-2 mt-1 border rounded-md" rows="3" required />
                    </div>
                    <button type="submit" className="px-4 py-2 font-semibold text-white bg-blue-500 rounded-md hover:bg-blue-600">Add Event</button>
                </form>
            )}

            <div className="space-y-4">
                {events.length > 0 ? (
                    events.map(event => (
                        <div key={event.id} className="p-4 border rounded-md">
                            <h4 className="text-xl font-semibold">{event.title} <span className="text-base text-gray-500 font-normal">({event.eventDate})</span></h4>
                            <p className="text-gray-700 mt-2">{event.description}</p>
                            <p className="text-xs text-gray-400 mt-1">Added: {new Date(event.createdAt).toLocaleDateString()}</p>
                        </div>
                    ))
                ) : (
                    <p className="text-gray-500">No timeline events added yet.</p>
                )}
            </div>
        </div>
    );
};

export default Timeline;