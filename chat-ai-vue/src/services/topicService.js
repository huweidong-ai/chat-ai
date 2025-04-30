let dialogTopics = [];

const getAllTopics = () => {
    return dialogTopics.slice().sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
};

const createTopic = (title, initialMessages = []) => {
    const newTopic = {
        id: dialogTopics.length + 1,
        title,
        createdAt: new Date(),
        messages: initialMessages
    };
    dialogTopics.push(newTopic);
    return newTopic;
};

const getTopicById = (id) => {
    return dialogTopics.find(dialog => dialog.id === id);
};

export default {
    getAllTopics,
    createTopic,
    getTopicById
};



