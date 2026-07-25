import { useState, useEffect, useRef } from 'react';
import { FiX } from 'react-icons/fi';
import { ModalFooter } from '@/shared/components/ModalFooter';
import { FormField } from '@/shared/components/form/FormField';
import { useConfirm } from '@/shared/confirm/useConfirm';
import { useFormKeyboardNav } from '@/shared/keyboard/useFormKeyboardNav';
import { collectErrors } from '@/shared/utils/validators';
import type { RoleGroup } from '../types/phanQuyenTypes';

interface Props {
    mode: 'create' | 'edit';
    group?: RoleGroup;
    onClose: () => void;
    onSubmit: (data: { code?: string; name: string; description?: string }) => void;
    isLoading: boolean;
}

/** Modal tạo mới hoặc chỉnh sửa tên/mô tả nhóm. */
const GroupFormModal = ({ mode, group, onClose, onSubmit, isLoading }: Props) => {
    const [code, setCode] = useState('');
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [errors, setErrors] = useState<Record<string, string>>({});

    const { confirmCreate, confirmSave } = useConfirm();

    const formRef = useRef<HTMLFormElement>(null);
    useFormKeyboardNav(formRef, {
        onSubmit: () => formRef.current?.requestSubmit(),
        onCancel: onClose,
    });

    useEffect(() => {
        if (mode === 'edit' && group) {
            setName(group.name);
            setDescription(group.description ?? '');
        }
    }, [mode, group]);

    const clearError = (key: string) =>
        setErrors((prev) => (prev[key] ? { ...prev, [key]: '' } : prev));

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        const found = collectErrors({
            code: mode === 'create' && !code.trim() ? 'Vui lòng nhập mã nhóm' : null,
            name: !name.trim() ? 'Vui lòng nhập tên nhóm' : null,
        });
        setErrors(found);
        if (Object.keys(found).length > 0) return;

        const ok = mode === 'create'
            ? await confirmCreate('nhóm quyền')
            : await confirmSave('nhóm quyền');
        if (!ok) return;

        onSubmit({
            ...(mode === 'create' ? { code: code.trim().toUpperCase() } : {}),
            name: name.trim(),
            description: description.trim() || undefined,
        });
    };

    const inp = 'w-full border border-gray-300 rounded-btn px-3 py-2 text-md focus:outline-none focus:border-primary';

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30">
            <div className="bg-white rounded-card shadow-xl w-full max-w-md p-6">
                {/* Header */}
                <div className="flex items-center justify-between mb-5">
                    <h2 className="text-lg font-semibold text-text-main">
                        {mode === 'create' ? 'Tạo nhóm mới' : 'Chỉnh sửa nhóm'}
                    </h2>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
                        <FiX size={18} />
                    </button>
                </div>

                <form ref={formRef} onSubmit={handleSubmit} noValidate className="space-y-4">
                    {mode === 'create' && (
                        <FormField label="Mã nhóm" required error={errors.code}>
                            <input
                                type="text"
                                value={code}
                                onChange={e => { setCode(e.target.value); clearError('code'); }}
                                placeholder="VD: SALES_STAFF"
                                className={inp}
                                maxLength={20}
                            />
                        </FormField>
                    )}

                    <FormField label="Tên nhóm" required error={errors.name}>
                        <input
                            type="text"
                            value={name}
                            onChange={e => { setName(e.target.value); clearError('name'); }}
                            placeholder="VD: Nhân viên kinh doanh"
                            className={inp}
                            maxLength={40}
                        />
                    </FormField>

                    <FormField label="Mô tả">
                        <textarea
                            value={description}
                            onChange={e => setDescription(e.target.value)}
                            placeholder="Mô tả ngắn về nhóm này..."
                            rows={2}
                            maxLength={50}
                            className={`${inp} resize-none`}
                        />
                    </FormField>

                    <ModalFooter onCancel={onClose} saving={isLoading} />
                </form>
            </div>
        </div>
    );
};

export default GroupFormModal;
